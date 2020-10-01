package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.History;
import com.anhvan.vmr.entity.TransferResponse;
import com.anhvan.vmr.exception.TransferException;
import com.anhvan.vmr.exception.TransferException.ErrorCode;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

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

    transfer(1, 2, 100000);

    return historyPromise.future();
  }

  public Future<TransferResponse> transfer(long senderId, long receiverId, long amount) {
    Promise<TransferResponse> responsePromise = Promise.promise();

    pool.getConnection(
        ar -> {
          if (ar.failed()) {
            log.error("Could not get connection", ar.cause());
            responsePromise.fail(ar.cause());
            return;
          }

          SqlConnection connection = ar.result();
          Transaction transaction = connection.begin();

          // Create holder to pass state throught all step
          TransferStateHolder holder =
              TransferStateHolder.builder()
                  .conn(connection)
                  .transaction(transaction)
                  .senderId(senderId)
                  .receiverId(receiverId)
                  .amount(amount)
                  .build();

          checkExist(holder)
              .compose(this::checkBalanceEnought)
              .compose(this::updateAccountBalance)
              .compose(this::writeTransfer)
              .compose(this::writeAccountLog)
              .onComplete(
                  transAr -> {
                    if (transAr.succeeded()) {
                      transaction.commit();
                      log.info("Transfer succeeded");
                    } else {
                      // Rollback all commit
                      transaction.rollback();
                      log.error("Error when transfer", transAr.cause());
                    }
                  });
        });

    return responsePromise.future();
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

  private Future<TransferStateHolder> checkBalanceEnought(TransferStateHolder holder) {
    Promise<TransferStateHolder> enoughtPromise = Promise.promise();

    SqlConnection conn = holder.getConn();

    conn.preparedQuery("select balance from users where id = ? for update")
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
                  } else {
                    holder.setSenderBalance(balance);
                  }
                }
                // Receiver check
                RowSet<Row> receiverResult = ar.result();
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

    SqlConnection conn = holder.getConn();

    conn.preparedQuery("update users set balance=balance+? where id=?")
        .executeBatch(
            Arrays.asList(
                Tuple.of(-holder.getAmount(), holder.getSenderId()),
                Tuple.of(holder.getAmount(), holder.getReceiverId())),
            ar -> {
              if (!ar.succeeded()) {
                balancePromise.fail(ar.cause());
                return;
              }
              holder.setNewSenderBalance(holder.getSenderBalance() - holder.getAmount());
              balancePromise.complete(holder);
            });

    return balancePromise.future();
  }

  private Future<TransferStateHolder> writeTransfer(TransferStateHolder holder) {
    Promise<TransferStateHolder> transferPromise = Promise.promise();
    transferPromise.complete();
    return transferPromise.future();
  }

  private Future<TransferStateHolder> writeAccountLog(TransferStateHolder holder) {
    Promise<TransferStateHolder> accountLogPromise = Promise.promise();
    accountLogPromise.complete();
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
  private long amount;
  private long senderBalance;
  private long receiverBalance;
  private long newSenderBalance; // after update balance
  private long newReceiverBalance; // after update balance
}
