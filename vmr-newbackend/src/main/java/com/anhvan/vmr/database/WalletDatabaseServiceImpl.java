package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.History;
import com.anhvan.vmr.entity.TransferRequest;
import com.anhvan.vmr.entity.TransferResponse;
import com.anhvan.vmr.exception.TransferException;
import com.anhvan.vmr.exception.TransferException.ErrorCode;
import com.anhvan.vmr.util.PasswordUtil;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
@AllArgsConstructor
@Log4j2
public class WalletDatabaseServiceImpl implements WalletDatabaseService {
  private static final String HISTORY_QUERY =
      "select transfers.sender, transfers.receiver, transfers.timestamp, transfers.amount, "
          + "transfers.message, account_logs.balance, account_logs.type as type_string, "
          + "account_logs.id from "
          + "account_logs inner join transfers "
          + "on account_logs.transfer = transfers.id "
          + "where account_logs.user = ?";

  private MySQLPool pool;
  private PasswordUtil passwordUtil;

  @Override
  public Future<List<History>> getHistory(long userId) {
    Promise<List<History>> historyPromise = Promise.promise();

    pool.preparedQuery(HISTORY_QUERY)
        .execute(
            Tuple.of(userId),
            ar -> {
              if (ar.succeeded()) {
                RowSet<Row> rowSet = ar.result();
                List<History> historyList = new ArrayList<>();
                for (Row row : rowSet) {
                  historyList.add(row2History(row));
                }
                historyPromise.complete(historyList);
              } else {
                Throwable cause = ar.cause();
                log.error("Error when get history of user {}", userId, cause);
                historyPromise.fail(cause);
              }
            });

    transfer(
        TransferRequest.builder()
            .sender(1)
            .receiver(2)
            .amount(1000)
            .message("Sample " + "message")
            .password("12345678")
            .requestId(3)
            .build());

    return historyPromise.future();
  }

  public Future<TransferResponse> transfer(TransferRequest transferRequest) {
    Promise<TransferResponse> responsePromise = Promise.promise();

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

    // Execute each step in transfer process
    checkPassword(initHolder)
        .compose(this::checkExist)
        .compose(this::checkBalanceEnought)
        .compose(this::checkRequestIdExist)
        .compose(this::updateAccountBalance)
        .compose(this::writeTransfer)
        .compose(this::writeAccountLog)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                initHolder.getTransaction().commit();
                initHolder.getConn().close();
              } else {
                log.error("Error when transfer", ar.cause());
                if (initHolder.getConn() != null) {
                  initHolder.getTransaction().rollback();
                  initHolder.getConn().close();
                }
              }
            });

    return responsePromise.future();
  }

  private Future<TransferStateHolder> checkPassword(TransferStateHolder holder) {
    Promise<TransferStateHolder> passwordPromise = Promise.promise();

    passwordUtil
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

              // Start new transaction
              pool.getConnection(
                  connAr -> {
                    if (ar.failed()) {
                      log.error("Could not get connection", ar.cause());
                      passwordPromise.fail(ar.cause());
                      return;
                    }

                    SqlConnection conn = connAr.result();
                    Transaction tx = conn.begin();

                    holder.setConn(conn);
                    holder.setTransaction(tx);

                    passwordPromise.complete(holder);
                  });
            });

    return passwordPromise.future();
  }

  private Future<TransferStateHolder> checkExist(TransferStateHolder holder) {
    Promise<TransferStateHolder> existPromise = Promise.promise();

    SqlConnection conn = holder.getConn();

    conn.preparedQuery("select exists(select * from users where id = ?) as user_exist")
        .execute(
            Tuple.of(holder.getReceiverId()),
            ar -> {
              if (ar.succeeded()) {
                for (Row row : ar.result()) {
                  // Check if receiver exist
                  if (row.getBoolean("user_exist")) {
                    existPromise.complete(holder);
                  } else {
                    existPromise.fail(
                        new TransferException("Receiver not exist", ErrorCode.RECEIVER_INVALID));
                  }
                }
              } else {
                existPromise.fail(ar.cause());
              }
            });

    return existPromise.future();
  }

  private Future<TransferStateHolder> checkRequestIdExist(TransferStateHolder holder) {
    Promise<TransferStateHolder> existPromise = Promise.promise();

    holder
        .getConn()
        .preparedQuery(
            "select exists(select * from transfers where sender=? and "
                + "request_id=?) as transfer_exist")
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

  private Future<TransferStateHolder> checkBalanceEnought(TransferStateHolder holder) {
    Promise<TransferStateHolder> enoughtPromise = Promise.promise();

    holder
        .getConn()
        .preparedQuery("select balance from users where id = ? for update")
        .executeBatch(
            Arrays.asList(Tuple.of(holder.getSenderId()), Tuple.of(holder.getReceiverId())),
            ar -> {
              if (ar.succeeded()) {
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
              } else {
                enoughtPromise.fail(ar.cause());
              }
            });

    return enoughtPromise.future();
  }

  private Future<TransferStateHolder> updateAccountBalance(TransferStateHolder holder) {
    Promise<TransferStateHolder> balancePromise = Promise.promise();

    long amount = holder.getAmount();
    long senderNewBalance = holder.getSenderBalance() - amount;
    long recevierNewBalance = holder.getReceiverBalance() + amount;

    holder
        .getConn()
        .preparedQuery("update users set balance=?, last_updated=? where id=?")
        .executeBatch(
            Arrays.asList(
                Tuple.of(senderNewBalance, holder.getLastUpdated(), holder.getSenderId()),
                Tuple.of(recevierNewBalance, holder.getLastUpdated(), holder.getReceiverId())),
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

  private Future<TransferStateHolder> writeTransfer(TransferStateHolder holder) {
    Promise<TransferStateHolder> transferPromise = Promise.promise();

    Tuple tuple =
        Tuple.of(
            holder.getSenderId(),
            holder.getReceiverId(),
            holder.getAmount(),
            holder.getMessage(),
            holder.getLastUpdated(),
            holder.getRequestId());

    holder
        .getConn()
        .preparedQuery(
            "insert into transfers (sender, receiver, amount, message, timestamp, "
                + "request_id) values (?,?,?,?,?,?)")
        .execute(
            tuple,
            ar -> {
              if (ar.failed()) {
                transferPromise.fail(ar.cause());
                return;
              }
              holder.setTransferId(ar.result().property(MySQLClient.LAST_INSERTED_ID));
              transferPromise.complete(holder);
            });

    return transferPromise.future();
  }

  private Future<TransferStateHolder> writeAccountLog(TransferStateHolder holder) {
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
        .preparedQuery("insert into account_logs (user, transfer, balance, type) values (?,?,?,?)")
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

  private History row2History(Row row) {
    History history = RowMapperUtil.mapRow(row, History.class);
    String typeString = row.getString("type_string");
    if (typeString.equals("transfer")) {
      history.setType(History.Type.TRANSFER);
    } else if (typeString.equals("receive")) {
      history.setType(History.Type.RECEIVE);
    }
    return history;
  }
}

@Getter
@Setter
@Builder
class TransferStateHolder {
  private SqlConnection conn;
  private Transaction transaction;
  private long receiverId;
  private long senderId;
  private long requestId;
  private long amount;
  private long senderBalance;
  private long receiverBalance;
  private long newSenderBalance; // after update balance
  private long newReceiverBalance; // after update balance
  private long lastUpdated;
  private String message;
  private long transferId;
  private String password;
}
