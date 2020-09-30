package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.History;
import com.anhvan.vmr.entity.TransferResponse;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
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
        });

    return responsePromise.future();
  }

  private Future<Boolean> checkExist(SqlConnection conn, long userId) {
    Promise<Boolean> existPromise = Promise.promise();

    conn.preparedQuery("select exists(select * from users where id = ?) as user_exist")
        .execute(
            Tuple.of(userId),
            ar -> {
              if (ar.succeeded()) {
                for (Row row : ar.result()) {
                  existPromise.complete(row.getBoolean("user_exist"));
                }
              } else {
                existPromise.fail(ar.cause());
              }
            });

    return existPromise.future();
  }

  private Future<Boolean> checkBalanceEnought(
      SqlConnection conn, long userId, long amount) {
    Promise<Boolean> existPromise = Promise.promise();

    conn.preparedQuery("select balance from users where id = ?")
        .execute(
            Tuple.of(userId),
            ar -> {
              if (ar.succeeded()) {
                for (Row row : ar.result()) {
                  long balance = row.getLong("balance");
                  existPromise.complete(balance >= amount);
                }
              } else {
                existPromise.fail(ar.cause());
              }
            });

    return existPromise.future();
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
