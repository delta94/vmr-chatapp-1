package com.anhvan.vmr.database;

import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import jodd.crypt.BCrypt;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class UserDBService {
  private MySQLPool pool;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public UserDBService(DatabaseService databaseService, AsyncWorkerUtil workerUtil) {
    pool = databaseService.getPool();
    this.workerUtil = workerUtil;
  }

  public Future<Integer> addUser(User user) {
    log.trace("Add user");
    Promise<Integer> idPromise = Promise.promise();
    workerUtil.execute(
        () -> {
          log.trace("Add user to database");
          String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
          Tuple info = Tuple.of(user.getUsername(), password, user.getName());
          pool.preparedQuery("insert into users(username, password, name) values (?,?,?)")
              .execute(
                  info,
                  rowSetRs -> {
                    log.info("execute query");
                    if (rowSetRs.succeeded()) {
                      RowSet<Row> rs = rowSetRs.result();
                      idPromise.complete(rs.property(MySQLClient.LAST_INSERTED_ID).intValue());
                    } else {
                      log.debug("Error when create user", rowSetRs.cause());
                      idPromise.fail(rowSetRs.cause());
                    }
                  });
        });
    return idPromise.future();
  }

  public Future<User> getUserByUsername(String username) {
    Promise<User> userPromise = Promise.promise();
    pool.preparedQuery("select * from users where username=?")
        .execute(
            Tuple.of(username),
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                log.trace("Here");
                RowSet<Row> result = rowSetRs.result();
                if (result.size() == 1) {
                  result.forEach(row -> userPromise.complete(rowToUser(row)));
                } else {
                  userPromise.fail("User not exist");
                }
              } else {
                log.error("Error when get user by username", rowSetRs.cause());
                userPromise.fail(rowSetRs.cause());
              }
            });
    return userPromise.future();
  }

  public Future<User> getUserById(int id) {
    Promise<User> userPromise = Promise.promise();
    pool.preparedQuery("select * from users where id=?")
        .execute(
            Tuple.of(id),
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                RowSet<Row> result = rowSetRs.result();
                if (result.size() == 1) {
                  result.forEach(row -> userPromise.complete(rowToUser(row)));
                } else {
                  userPromise.fail("User not exist");
                }
              } else {
                log.error("Fail when get user", rowSetRs.cause());
                userPromise.fail(rowSetRs.cause());
              }
            });
    return userPromise.future();
  }

  public Future<List<User>> getListUser() {
    Promise<List<User>> listUserPromise = Promise.promise();
    List<User> userList = new ArrayList<>();
    pool.query("select * from users")
        .execute(
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                RowSet<Row> result = rowSetRs.result();
                result.forEach(row -> userList.add(rowToUser(row)));
              } else {
                log.debug("Fail when get user list", rowSetRs.cause());
                listUserPromise.fail(rowSetRs.cause());
              }
              listUserPromise.complete(userList);
            });
    return listUserPromise.future();
  }

  private User rowToUser(Row row) {
    String password = row.getString("password");
    int id = row.getInteger("id");
    String name = row.getString("name");
    String username = row.getString("username");
    return User.builder()
        .username(username)
        .password(password)
        .id(id)
        .name(name)
        .active(true)
        .build();
  }
}
