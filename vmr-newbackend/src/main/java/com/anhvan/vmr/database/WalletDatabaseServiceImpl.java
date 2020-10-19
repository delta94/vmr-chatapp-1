package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.HistoryItemResponse;
import com.anhvan.vmr.exception.TransferException;
import com.anhvan.vmr.exception.TransferException.ErrorCode;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.PasswordService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class WalletDatabaseServiceImpl implements WalletDatabaseService {
  public static final String HISTORY_QUERY =
      "select transfers.sender, transfers.receiver, transfers.timestamp, transfers.amount, "
          + "transfers.message, account_logs.balance, account_logs.type as type_string, "
          + "account_logs.id from "
          + "account_logs inner join transfers "
          + "on account_logs.transfer = transfers.id "
          + "where account_logs.user = ?";

  public static final String CHECK_USER_EXIST_QUERY =
      "select exists(select * from users where id = ?) as user_exist";

  public static final String CHECK_REQUEST_ID_EXIST_QUERY =
      "select exists(select * from transfers where sender=? and "
          + "request_id=?) as transfer_exist";

  public static final String BALANCE_QUERY = "select balance from users where id = ?";

  public static final String UPDATE_BALANCE_QUERY =
      "update users set balance=?, last_updated=? where id=?";

  public static final String CREATE_TRANSFER_QUERY =
      "insert into transfers (sender, receiver, amount, message, timestamp, "
          + "request_id) values (?,?,?,?,?,?)";

  public static final String CREATE_ACCOUNT_LOG_QUERY =
      "insert into account_logs (user, transfer, balance, type) values (?,?,?,?)";

  public static final String WRITE_CHAT_STMT =
      "insert into messages (sender, receiver, send_time, message, type, transfer_id) "
          + "values (?,?,?,?,?,?)";

  public static final String REQUIRE_LOCKS_STMT =
      "select * from users where id=? or id=? for update";

  public static final String GET_HISTORY_WITH_OFFSET_STMT =
      "select transfers.sender, transfers.receiver, transfers.timestamp, transfers.amount, "
          + "transfers.message, account_logs.balance, account_logs.type as type_string, "
          + "account_logs.id from "
          + "account_logs inner join transfers "
          + "on account_logs.transfer = transfers.id "
          + "where account_logs.user = ? "
          + "order by account_logs.id desc limit ?, 20 ";

  private MySQLPool pool;
  private PasswordService passwordService;
  private ChatDatabaseService chatDatabaseService;

  @Override
  public Future<List<HistoryItemResponse>> getHistoryWithOffset(long userId, long offset) {
    log.debug("Start getHistoryWithOffset: userId={}, offset={}", userId, offset);

    Promise<List<HistoryItemResponse>> historyPromise = Promise.promise();

    pool.preparedQuery(GET_HISTORY_WITH_OFFSET_STMT)
        .execute(
            Tuple.of(userId, offset),
            ar -> {
              if (ar.succeeded()) {
                RowSet<Row> rowSet = ar.result();
                List<HistoryItemResponse> historyList = new ArrayList<>();
                for (Row row : rowSet) {
                  historyList.add(HistoryItemResponse.fromRow(row));
                }
                historyPromise.complete(historyList);
              } else {
                Throwable cause = ar.cause();
                log.error("Error when get history: userId={}, offset={}", userId, offset, cause);
                historyPromise.fail(cause);
              }
            });

    return historyPromise.future();
  }

  public Future<DatabaseTransferResponse> transfer(DatabaseTransferRequest transferRequest) {
    log.debug("Start transfer: transferRequest={}", transferRequest);

    Promise<DatabaseTransferResponse> responsePromise = Promise.promise();

    // Create holder to pass state throught all step
    TransferStateHolder initHolder =
        TransferStateHolder.builder()
            .senderId(transferRequest.getSender())
            .receiverId(transferRequest.getReceiver())
            .amount(transferRequest.getAmount())
            .requestId(transferRequest.getRequestId())
            .lastUpdated(Instant.now().getEpochSecond())
            .message(transferRequest.getMessage())
            .password(transferRequest.getPassword())
            .build();

    // Execute each step of transfer process
    checkPassword(initHolder)
        .compose(this::startTransaction)
        .compose(this::checkReceiverExist)
        .compose(this::lockSenderAndReceiver)
        .compose(this::checkBalanceEnough)
        .compose(this::checkRequestIdExist)
        .compose(this::updateAccountBalance)
        .compose(this::writeTransfer)
        .compose(this::writeAccountLog)
        .compose(this::addChatMessage)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                // Transfer successfully
                initHolder.getTransaction().commit();
                initHolder.getConn().close();

                log.debug("transfer execute successfully: transferRequest={}", transferRequest);

                // Return new balance and last update time to sender
                responsePromise.complete(
                    DatabaseTransferResponse.builder()
                        .newBalance(initHolder.getNewSenderBalance())
                        .lastUpdated(initHolder.getLastUpdated())
                        .build());
              } else {
                Throwable cause = ar.cause();

                log.error("transfer execute failed: transferRequest={}", transferRequest, cause);

                // Rollback and close connection
                if (initHolder.getConn() != null) {
                  initHolder.getTransaction().rollback();
                  initHolder.getConn().close();
                }

                // Return error
                responsePromise.fail(cause);
              }
            });

    return responsePromise.future();
  }

  Future<TransferStateHolder> checkPassword(TransferStateHolder holder) {
    log.debug(
        "Start checkPassword: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> passwordPromise = Promise.promise();

    passwordService
        .checkPassword(holder.getSenderId(), holder.getPassword())
        .onComplete(
            ar -> {
              // Check password failed
              if (ar.failed()) {
                passwordPromise.fail(ar.cause());
                return;
              }

              // Password not valid
              if (!ar.result()) {
                passwordPromise.fail(
                    new TransferException("Password not valid", ErrorCode.PASSWORD_INVALID));
                return;
              }

              passwordPromise.complete(holder);
            });

    return passwordPromise.future();
  }

  Future<TransferStateHolder> startTransaction(TransferStateHolder holder) {
    log.debug(
        "Start startTransaction: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> txPromise = Promise.promise();

    pool.getConnection(
        ar -> {
          if (ar.failed()) {
            log.error("Could not get connection", ar.cause());
            txPromise.fail(ar.cause());
            return;
          }

          // Create connection and start a transaction
          SqlConnection conn = ar.result();
          Transaction tx = conn.begin();

          // Add connection to holder
          holder.setConn(conn);
          holder.setTransaction(tx);
          log.debug(
              "Create connection success: sender={}, receiver={}",
              holder.getSenderId(),
              holder.getReceiverId());

          txPromise.complete(holder);
        });

    return txPromise.future();
  }

  Future<TransferStateHolder> checkReceiverExist(TransferStateHolder holder) {
    log.debug(
        "Start checkReceiverExist, userId={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> existPromise = Promise.promise();

    holder
        .getConn()
        .preparedQuery(CHECK_USER_EXIST_QUERY)
        .execute(
            Tuple.of(holder.getReceiverId()),
            ar -> {
              if (ar.failed()) {
                existPromise.fail(ar.cause());
                return;
              }

              for (Row row : ar.result()) {
                // Check if receiver exist
                if (row.getBoolean("user_exist")) {
                  existPromise.complete(holder);
                } else {
                  existPromise.fail(
                      new TransferException("Receiver not exist", ErrorCode.RECEIVER_INVALID));
                }
              }
            });

    return existPromise.future();
  }

  Future<TransferStateHolder> checkRequestIdExist(TransferStateHolder holder) {
    log.debug(
        "Start checkRequestIdExist, userId={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> existPromise = Promise.promise();

    holder
        .getConn()
        .preparedQuery(CHECK_REQUEST_ID_EXIST_QUERY)
        .execute(
            Tuple.of(holder.getSenderId(), holder.getRequestId()),
            ar -> {
              if (ar.failed()) {
                existPromise.fail(ar.cause());
                return;
              }

              for (Row row : ar.result()) {
                if (row.getBoolean("transfer_exist")) {
                  existPromise.fail(
                      new TransferException("Request id existed", ErrorCode.REQUEST_EXISTED));
                  return;
                }
                existPromise.complete(holder);
              }
            });

    return existPromise.future();
  }

  Future<TransferStateHolder> lockSenderAndReceiver(TransferStateHolder holder) {
    Promise<TransferStateHolder> lockPromise = Promise.promise();

    log.debug(
        "Lock sender and receiver: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    holder
        .getConn()
        .preparedQuery(REQUIRE_LOCKS_STMT)
        .execute(
            Tuple.of(holder.getSenderId(), holder.getReceiverId()),
            ar -> {
              if (ar.succeeded()) {
                log.debug(
                    "Require lock success: sender={}, receiver={}",
                    holder.getSenderId(),
                    holder.getReceiverId());
                lockPromise.complete(holder);
              } else {
                lockPromise.fail(ar.cause());
              }
            });

    return lockPromise.future();
  }

  Future<TransferStateHolder> checkBalanceEnough(TransferStateHolder holder) {
    Promise<TransferStateHolder> enoughtPromise = Promise.promise();

    log.debug(
        "Check balance enought: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    holder
        .getConn()
        .preparedQuery(BALANCE_QUERY)
        .executeBatch(
            Arrays.asList(Tuple.of(holder.getSenderId()), Tuple.of(holder.getReceiverId())),
            ar -> {
              if (ar.failed()) {
                enoughtPromise.fail(ar.cause());
                return;
              }

              // Sender checking
              RowSet<Row> senderResult = ar.result();
              for (Row row : senderResult) {
                long balance = row.getLong("balance");
                if (balance < holder.getAmount()) {
                  enoughtPromise.fail(
                      new TransferException(
                          "Balance is not enought", ErrorCode.BALANCE_NOT_ENOUGHT));
                  return;
                }
                holder.setSenderBalance(balance);
              }

              // Receiver check
              RowSet<Row> receiverResult = senderResult.next();
              for (Row row : receiverResult) {
                holder.setReceiverBalance(row.getLong("balance"));
              }
              enoughtPromise.complete(holder);
            });

    return enoughtPromise.future();
  }

  Future<TransferStateHolder> updateAccountBalance(TransferStateHolder holder) {
    log.debug(
        "Start updateAccountBalance: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> balancePromise = Promise.promise();

    long amount = holder.getAmount();
    long senderNewBalance = holder.getSenderBalance() - amount;
    long recevierNewBalance = holder.getReceiverBalance() + amount;
    long lastUpdated = holder.getLastUpdated();

    List<Tuple> queryTuples =
        Arrays.asList(
            Tuple.of(senderNewBalance, lastUpdated, holder.getSenderId()),
            Tuple.of(recevierNewBalance, lastUpdated, holder.getReceiverId()));

    holder
        .getConn()
        .preparedQuery(UPDATE_BALANCE_QUERY)
        .executeBatch(
            queryTuples,
            ar -> {
              if (ar.failed()) {
                balancePromise.fail(ar.cause());
                return;
              }
              holder.setNewSenderBalance(senderNewBalance);
              holder.setNewReceiverBalance(recevierNewBalance);
              balancePromise.complete(holder);
            });

    return balancePromise.future();
  }

  Future<TransferStateHolder> writeTransfer(TransferStateHolder holder) {
    log.debug(
        "Start writeTransfer: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> transferPromise = Promise.promise();

    Tuple transferTuple =
        Tuple.of(
            holder.getSenderId(),
            holder.getReceiverId(),
            holder.getAmount(),
            holder.getMessage(),
            holder.getLastUpdated(),
            holder.getRequestId());

    holder
        .getConn()
        .preparedQuery(CREATE_TRANSFER_QUERY)
        .execute(
            transferTuple,
            ar -> {
              // Transfer failed
              if (ar.failed()) {
                transferPromise.fail(ar.cause());
                return;
              }

              // Save transfer id
              holder.setTransferId(ar.result().property(MySQLClient.LAST_INSERTED_ID));
              transferPromise.complete(holder);
            });

    return transferPromise.future();
  }

  Future<TransferStateHolder> writeAccountLog(TransferStateHolder holder) {
    log.debug(
        "Start writeAccountLog: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> accountLogPromise = Promise.promise();

    List<Tuple> tuples =
        Arrays.asList(
            Tuple.of(
                holder.getSenderId(),
                holder.getTransferId(),
                holder.getNewSenderBalance(),
                "TRANSFER"),
            Tuple.of(
                holder.getReceiverId(),
                holder.getTransferId(),
                holder.getNewReceiverBalance(),
                "RECEIVE"));

    holder
        .getConn()
        .preparedQuery(CREATE_ACCOUNT_LOG_QUERY)
        .executeBatch(
            tuples,
            ar -> {
              if (ar.failed()) {
                accountLogPromise.fail(ar.cause());
                return;
              }

              accountLogPromise.complete(holder);
            });

    return accountLogPromise.future();
  }

  Future<TransferStateHolder> addChatMessage(TransferStateHolder holder) {
    log.debug(
        "Start addChatMessage: sender={}, receiver={}",
        holder.getSenderId(),
        holder.getReceiverId());

    Promise<TransferStateHolder> chatPromise = Promise.promise();

    Message message =
        Message.builder()
            .senderId(holder.getSenderId())
            .receiverId(holder.getReceiverId())
            .message(holder.getAmount() + ";" + holder.getMessage())
            .timestamp(holder.getLastUpdated())
            .transferId(holder.getTransferId())
            .type("TRANSFER")
            .build();

    chatDatabaseService
        .addChat(message, holder.getTransaction())
        .onComplete(
            ar -> {
              if (ar.failed()) {
                chatPromise.fail(ar.cause());
                return;
              }

              holder.setLastMessageId(ar.result());
              chatPromise.complete(holder);
            });

    return chatPromise.future();
  }
}
