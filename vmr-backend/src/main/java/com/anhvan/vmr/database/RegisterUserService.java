package com.anhvan.vmr.database;

import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.mysqlclient.MySQLClient;
import io.vertx.reactivex.mysqlclient.MySQLPool;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.reactivex.sqlclient.Tuple;

public class RegisterUserService implements Handler<Message<JsonObject>> {
  private final MySQLPool connPool;

  public RegisterUserService(MySQLPool connPool) {
    this.connPool = connPool;
  }

  @Override
  public void handle(Message<JsonObject> msg) {
    JsonObject userInfo = msg.body();
    Single<SqlConnection> connectionSingle = connPool.rxGetConnection();
    connectionSingle
        .subscribe(
            conn ->
                checkExist(conn, userInfo.getString("username"))
                    .rxOnComplete()
                    .subscribe(
                        exist -> handleCheckExist(exist, msg, conn),
                        failue -> msg.fail(1, "Error when add user"))
                    .isDisposed())
        .isDisposed();
  }

  private void handleCheckExist(boolean exist, Message<JsonObject> msg, SqlConnection conn) {
    if (exist) {
      msg.reply(new JsonObject().put("status", "exist"));
      conn.close();
      return;
    }
    addUser(conn, msg.body())
        .rxOnComplete()
        .subscribe(
            newId -> msg.reply(new JsonObject().put("status", "ok").put("userId", newId)),
            failue -> msg.fail(1, "Error when add user"))
        .isDisposed();
  }

  private Future<Boolean> checkExist(SqlConnection conn, String username) {
    Promise<Boolean> result = Promise.promise();
    conn.preparedQuery("select id from users where username=?")
        .rxExecute(Tuple.of(username))
        .subscribe(resultSet -> result.complete(resultSet.size() == 1), result::fail)
        .isDisposed();
    return result.future();
  }

  private Future<Integer> addUser(SqlConnection conn, JsonObject userInfo) {
    Promise<Integer> createdId = Promise.promise();
    conn.preparedQuery("insert into users (username, password, name) values (?,?,?)")
        .rxExecute(
            Tuple.of(
                userInfo.getString("username"),
                userInfo.getString("password"),
                userInfo.getString("name")))
        .subscribe(
            resultSet -> {
              createdId.complete(resultSet.property(MySQLClient.LAST_INSERTED_ID).intValue());
              conn.close();
            },
            failue -> {
              createdId.fail(failue);
              conn.close();
            })
        .isDisposed();
    return createdId.future();
  }
}
