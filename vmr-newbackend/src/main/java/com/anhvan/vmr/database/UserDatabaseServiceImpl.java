package com.anhvan.vmr.database;

import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import com.anhvan.vmr.util.RowMapperUtil;
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
public class UserDatabaseServiceImpl implements UserDatabaseService {
  public static final String INSERT_USER =
      "insert into users(username, password, name) values (?,?,?)";

  public static final String GET_ALL_USER = "select username, name, id, is_active from users";

  public static final String FIND_BY_USERNAME = "select * from users where username=?";

  public static final String SELECT_BY_ID = "select * from users where id=?";

  public static final String FIND_USER_FULLTEXT =
      "select * from users where match(username, name) against (? IN NATURAL LANGUAGE MODE)";

  private MySQLPool pool;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public UserDatabaseServiceImpl(DatabaseService databaseService, AsyncWorkerUtil workerUtil) {
    pool = databaseService.getPool();
    this.workerUtil = workerUtil;
  }

  @Override
  public Future<Long> addUser(User user) {
    log.info("Add user {} to database", user.getUsername());

    Promise<Long> idPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          // Generate password
          String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

          // Info params
          Tuple info = Tuple.of(user.getUsername(), password, user.getName());

          // Trace
          log.trace("Pool will execute query");

          // Execute query
          pool.preparedQuery(INSERT_USER)
              .execute(
                  info,
                  rowSetRs -> {
                    if (rowSetRs.succeeded()) {
                      RowSet<Row> rs = rowSetRs.result();
                      idPromise.complete(rs.property(MySQLClient.LAST_INSERTED_ID));
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
                RowSet<Row> result = rowSetRs.result();
                if (result.size() == 1) {
                  result.forEach(row -> userPromise.complete(rowToUser(row)));
                } else {
                  log.info("Fail when get user with username:{} from database", username);
                  userPromise.fail("User not exist");
                }
              } else {
                log.error("Error when get user by username: {}", username, rowSetRs.cause());
                userPromise.fail(rowSetRs.cause());
              }
            });

    return userPromise.future();
  }

  @Override
  public Future<User> getUserById(long id) {
    Promise<User> userPromise = Promise.promise();
    pool.preparedQuery(SELECT_BY_ID)
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

    pool.query(GET_ALL_USER)
        .execute(
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                List<User> userList = new ArrayList<>();
                RowSet<Row> result = rowSetRs.result();
                result.forEach(row -> userList.add(rowToUser(row)));
                listUserPromise.complete(userList);
              } else {
                log.error("Fail when get user list", rowSetRs.cause());
                listUserPromise.fail(rowSetRs.cause());
              }
            });

    return listUserPromise.future();
  }

  @Override
  public Future<List<User>> queryListUser(String query) {
    log.debug("Query user with full text search");

    Promise<List<User>> userListPromise = Promise.promise();

    pool.preparedQuery(FIND_USER_FULLTEXT)
        .execute(
            Tuple.of(query),
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                List<User> userList = new ArrayList<>();
                RowSet<Row> result = rowSetRs.result();
                result.forEach(row -> userList.add(rowToUser(row)));
                userListPromise.complete(userList);
              } else {
                log.error("Fail to query user", rowSetRs.cause());
              }
            });

    return userListPromise.future();
  }

  private User rowToUser(Row row) {
    return RowMapperUtil.mapRow(row, User.class);
  }
}
