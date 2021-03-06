package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import jodd.crypt.BCrypt;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class UserDatabaseServiceImpl implements UserDatabaseService {
  public static final String INSERT_USER_STMT =
      "insert into users(username, password, name, last_updated) values (?,?,?,?)";

  public static final String FIND_ALL_USER_STMT = "select username, name, id, is_active from users";

  public static final String FIND_BY_USERNAME_STMT = "select * from users where username=?";

  public static final String FIND_BY_ID_STMT = "select * from users where id=?";

  public static final String FIND_USER_FULL_TEXT_STMT =
      "select t1.username, t1.name, t1.id, t2.status "
          + "from (select * from users where match(username, name) against(? IN NATURAL LANGUAGE MODE)) t1 "
          + "left join (select * from friends where user_id = ?) t2 "
          + "on t1.id = t2.friend_id "
          + "limit 10";

  private SqlClient pool;
  private AsyncWorkerService workerUtil;

  public UserDatabaseServiceImpl(SqlClient sqlClient, AsyncWorkerService workerUtil) {
    pool = sqlClient;
    this.workerUtil = workerUtil;
  }

  @Override
  public Future<Long> addUser(User user) {
    log.info("addUser: username={}", user.getUsername());

    Promise<Long> idPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          // Generate password
          String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(5));

          // Info params
          Tuple info =
              Tuple.of(
                  user.getUsername(), password, user.getName(), Instant.now().getEpochSecond());

          // Execute query
          pool.preparedQuery(INSERT_USER_STMT)
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

    pool.preparedQuery(FIND_BY_USERNAME_STMT)
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
    pool.preparedQuery(FIND_BY_ID_STMT)
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

    pool.query(FIND_ALL_USER_STMT)
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
  public Future<List<Friend>> queryListUserWithFriendStatus(String query, long userId) {
    Promise<List<Friend>> userListPromise = Promise.promise();

    pool.preparedQuery(FIND_USER_FULL_TEXT_STMT)
        .execute(
            Tuple.of(query, userId),
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                List<Friend> userList = new ArrayList<>();
                RowSet<Row> result = rowSetRs.result();
                result.forEach(row -> userList.add(RowMapperUtil.mapRow(row, Friend.class)));
                userListPromise.complete(userList);
              } else {
                log.error(
                    "Fail to query user: query={}, userId={}", query, userId, rowSetRs.cause());
              }
            });

    return userListPromise.future();
  }

  private User rowToUser(Row row) {
    return RowMapperUtil.mapRow(row, User.class);
  }
}
