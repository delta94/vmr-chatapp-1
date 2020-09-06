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
public class UserDBServiceImpl implements UserDBService {
  public static final String INSERT_USER =
      "insert into users(username, password, name) values (?,?,?)";

  public static final String GET_ALL_USER = "select * from users";

  public static final String FIND_BY_USERNAME = "select * from users where username=?";

  private MySQLPool pool;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public UserDBServiceImpl(DatabaseService databaseService, AsyncWorkerUtil workerUtil) {
    pool = databaseService.getPool();
    this.workerUtil = workerUtil;
  }

  @Override
  public Future<Integer> addUser(User user) {
    log.info("Add user {} to database", user.getUsername());

    Promise<Integer> idPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          // Generate password
          String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

          // Info params
          Tuple info = Tuple.of(user.getUsername(), password, user.getName());

          // Execute query
          pool.preparedQuery(INSERT_USER)
              .execute(
                  info,
                  rowSetRs -> {
                    if (rowSetRs.succeeded()) {
                      RowSet<Row> rs = rowSetRs.result();
                      idPromise.complete(rs.property(MySQLClient.LAST_INSERTED_ID).intValue());
                    } else {
                      log.error("Error when create user {}", user.getUsername(), rowSetRs.cause());
                      idPromise.fail(rowSetRs.cause());
                    }
                  });
        });

    return idPromise.future();
  }

  @Override
  public Future<User> getUserByUsername(String username) {
    log.debug("Get user {} by username", username);

    Promise<User> userPromise = Promise.promise();

    pool.preparedQuery(FIND_BY_USERNAME)
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

  @Override
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

  @Override
  public Future<List<User>> getListUser() {
    log.debug("Get list of all user from database");

    Promise<List<User>> listUserPromise = Promise.promise();

    List<User> userList = new ArrayList<>();

    pool.query(GET_ALL_USER)
        .execute(
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                RowSet<Row> result = rowSetRs.result();
                result.forEach(row -> userList.add(rowToUser(row)));
              } else {
                log.error("Fail when get user list", rowSetRs.cause());
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
