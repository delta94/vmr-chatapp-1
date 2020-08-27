package com.anhvan.vmr.database;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import jodd.crypt.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UserDBService {
  private static final Logger logger = LogManager.getLogger(UserDBService.class);

  private MySQLPool pool;

  @Inject
  public UserDBService(DatabaseService databaseService) {
    pool = databaseService.getPool();
  }

  public Future<Integer> addUser(User user) {
    Promise<Integer> result = Promise.promise();
    String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
    Tuple info = Tuple.of(user.getUsername(), password, user.getName());
    pool.preparedQuery("insert into users(username, password, name) values (?,?,?)")
        .execute(
            info,
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                RowSet<Row> rs = rowSetRs.result();
                result.complete(rs.property(MySQLClient.LAST_INSERTED_ID).intValue());
              } else {
                logger.debug("Error when create user", rowSetRs.cause());
                result.fail(rowSetRs.cause());
              }
            });
    return result.future();
  }

  public Future<User> getUserByUsername(String username) {
    Promise<User> userPromise = Promise.promise();
    pool.preparedQuery("select * from users where username=?")
        .execute(
            Tuple.of(username),
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                RowSet<Row> result = rowSetRs.result();
                if (result.size() == 1) {
                  result.forEach(row -> userPromise.complete(rowToUser(row)));
                } else {
                  userPromise.fail("User not exist");
                }
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
