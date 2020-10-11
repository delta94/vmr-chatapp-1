package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.GrpcUserResponse;
import com.anhvan.vmr.exception.AddFriendException;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class FriendDatabaseServiceImpl implements FriendDatabaseService {
  private static final String ADD_FRIEND_STMT =
      "insert into friends(user_id, friend_id, status) values(?,?,?)";

  private static final String GET_FRIENDS_STMT =
      "select users.id, users.username, users.name, friends.status "
          + "from users "
          + "inner join friends on users.id = friends.friend_id "
          + "where friends.user_id = ?";

  private static final String GET_FRIENDS_WITH_MESSAGE_STMT =
      "select users.id, users.username, users.name, messages.message as last_message, "
          + "messages.sender as last_message_sender, messages.type as last_message_type, "
          + "messages.send_time as last_message_timestamp, friends.num_unread_message "
          + "from users inner join friends "
          + "on users.id = friends.friend_id "
          + "left join messages on messages.id = friends.last_message_id "
          + "where friends.user_id = ? and friends.status='ACCEPTED'";

  public static final String ACCEPT_FRIEND =
      "update friends set status='ACCEPTED' where user_id=? and friend_id=?";

  public static final String CLEAR_UNREAD_MESSAGE_STMT =
      "update friends set num_unread_message=0 where user_id=? and friend_id=?";

  public static final String REJECT_FRIEND_STMT =
      "delete from friends where (user_id=? and friend_id=?)";

  public static final String UPDATE_FRIEND_STMT =
      "update friends set status=? where user_id=? and friend_id=?";

  private MySQLPool pool;
  private DatabaseService dbService;

  @Inject
  public FriendDatabaseServiceImpl(DatabaseService databaseService) {
    pool = databaseService.getPool();
    dbService = databaseService;
  }

  @Override
  public Future<Void> addFriend(long userId, long friendId) {
    Promise<Void> addFriendPromise = Promise.promise();

    // Prepared tuples
    List<Tuple> tuples =
        Arrays.asList(
            Tuple.of(userId, friendId, "WAITING"), Tuple.of(friendId, userId, "NOT_ANSWER"));

    TransactionManager transactionManager = dbService.getTransactionManager();

    transactionManager
        .begin()
        .compose(
            // Check current friend status
            manager -> {
              Promise<TransactionManager> promise = Promise.promise();

              manager
                  .getTransaction()
                  .preparedQuery("select status from friends where user_id=? and friend_id=?")
                  .execute(
                      Tuple.of(userId, friendId),
                      ar -> {
                        // Fail to execute query
                        if (ar.failed()) {
                          promise.fail(ar.cause());
                          return;
                        }

                        // Not add friend yet
                        if (ar.result().size() == 0) {
                          manager.set("STATUS", "NOTHING");
                          promise.complete(manager);
                          return;
                        }

                        // Aldready exist in table
                        Row result = RowMapperUtil.firstRow(ar.result());
                        if (result != null) {
                          String status = result.getString("status");
                          switch (status) {
                            case "ACCEPTED":
                              promise.fail(
                                  new AddFriendException(
                                      "Aldready added", AddFriendException.ErrorCode.ACCEPTED));
                              break;
                            case "WAITING":
                              promise.fail(
                                  new AddFriendException(
                                      "Waiting", AddFriendException.ErrorCode.WAITING));
                              break;
                            case "NOT_ANSWER":
                              promise.fail(
                                  new AddFriendException(
                                      "Wait for accepted",
                                      AddFriendException.ErrorCode.NOT_ANSWER));
                              break;
                            default:
                              manager.set("STATUS", "REMOVED");
                              promise.complete(manager);
                              break;
                          }
                        }
                      });

              return promise.future();
            })
        .compose(
            manager -> {
              Promise<TransactionManager> promise = Promise.promise();

              String stmt = ADD_FRIEND_STMT;
              if (manager.get("STATUS").equals("REMOVED")) {
                stmt = UPDATE_FRIEND_STMT;
              }

              manager
                  .getTransaction()
                  .preparedQuery(stmt)
                  .executeBatch(
                      tuples,
                      ar -> {
                        if (ar.failed()) {
                          promise.fail(ar.cause());
                        } else {
                          promise.complete(manager);
                        }
                      });

              return promise.future();
            })
        .compose(TransactionManager::commit)
        .onComplete(
            ar -> {
              transactionManager.close();
              if (ar.failed()) {
                log.error(
                    "Error when add friend userId:{}, friendId:{}", userId, friendId, ar.cause());
                addFriendPromise.fail(ar.cause());
              } else {
                addFriendPromise.complete();
              }
            });

    return addFriendPromise.future();
  }

  @Override
  public Future<List<GrpcUserResponse>> getFriendList(long userId) {
    Promise<List<GrpcUserResponse>> friendListPromise = Promise.promise();

    pool.preparedQuery(GET_FRIENDS_STMT)
        .execute(
            Tuple.of(userId),
            rowSetAsyncRs -> {
              if (rowSetAsyncRs.succeeded()) {
                RowSet<Row> rowSet = rowSetAsyncRs.result();
                List<GrpcUserResponse> userList = new ArrayList<>();
                for (Row row : rowSet) {
                  userList.add(RowMapperUtil.mapRow(row, GrpcUserResponse.class));
                }
                friendListPromise.complete(userList);
              } else {
                Throwable cause = rowSetAsyncRs.cause();
                log.error("Error when get friend list of user {}", userId, cause);
                friendListPromise.fail(cause);
              }
            });

    return friendListPromise.future();
  }

  @Override
  public Future<Void> acceptFriend(long invitorId, long userId) {
    Promise<Void> statusPromise = Promise.promise();

    // Get transaction manager object
    TransactionManager transactionManager = dbService.getTransactionManager();

    transactionManager
        .begin()
        .compose(
            manager -> {
              Promise<TransactionManager> promise = Promise.promise();
              manager
                  .getTransaction()
                  .preparedQuery(ACCEPT_FRIEND)
                  .executeBatch(
                      Arrays.asList(Tuple.of(invitorId, userId), Tuple.of(userId, invitorId)),
                      ar -> {
                        if (ar.succeeded()) {
                          promise.complete(manager);
                        } else {
                          promise.fail(ar.cause());
                        }
                      });
              return promise.future();
            })
        .compose(TransactionManager::commit)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                log.debug("Accept friend successfully");
                statusPromise.complete();
              } else {
                log.debug("Error when add friend", ar.cause());
                transactionManager.close();
                statusPromise.fail(ar.cause());
              }
            });

    return statusPromise.future();
  }

  @Override
  public Future<Void> rejectFriend(long invitorId, long userId) {
    Promise<Void> statusPromise = Promise.promise();

    // Get transaction manager object
    TransactionManager transactionManager = dbService.getTransactionManager();

    transactionManager
        .begin()
        .compose(
            manager -> {
              Promise<TransactionManager> promise = Promise.promise();
              manager
                  .getTransaction()
                  .preparedQuery(REJECT_FRIEND_STMT)
                  .executeBatch(
                      Arrays.asList(Tuple.of(invitorId, userId), Tuple.of(userId, invitorId)),
                      ar -> {
                        if (ar.succeeded()) {
                          promise.complete(manager);
                        } else {
                          promise.fail(ar.cause());
                        }
                      });
              return promise.future();
            })
        .compose(TransactionManager::commit)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                log.debug("Accept friend successfully");
                statusPromise.complete();
              } else {
                log.debug("Error when reject friend", ar.cause());
                transactionManager.close();
                statusPromise.fail(ar.cause());
              }
            });

    return statusPromise.future();
  }

  @Override
  public Future<List<GrpcUserResponse>> getChatFriendList(long userId) {
    Promise<List<GrpcUserResponse>> friendListPromise = Promise.promise();

    pool.preparedQuery(GET_FRIENDS_WITH_MESSAGE_STMT)
        .execute(
            Tuple.of(userId),
            rowSetAsyncRs -> {
              if (rowSetAsyncRs.succeeded()) {
                RowSet<Row> rowSet = rowSetAsyncRs.result();
                List<GrpcUserResponse> userList = new ArrayList<>();
                for (Row row : rowSet) {
                  userList.add(RowMapperUtil.mapRow(row, GrpcUserResponse.class));
                }
                friendListPromise.complete(userList);
              } else {
                Throwable cause = rowSetAsyncRs.cause();
                log.error("Error when get friend list of user {}", userId, cause);
                friendListPromise.fail(cause);
              }
            });

    return friendListPromise.future();
  }

  @Override
  public Future<Void> clearUnreadMessage(long userId, long friendId) {
    Promise<Void> queryPromise = Promise.promise();

    pool.preparedQuery(CLEAR_UNREAD_MESSAGE_STMT)
        .execute(
            Tuple.of(userId, friendId),
            ar -> {
              if (ar.failed()) {
                queryPromise.fail(ar.cause());
                return;
              }
              queryPromise.complete();
            });

    return queryPromise.future();
  }
}
