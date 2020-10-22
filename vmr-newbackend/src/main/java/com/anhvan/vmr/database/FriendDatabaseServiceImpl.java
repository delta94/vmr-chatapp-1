package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.exception.AddFriendException;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class FriendDatabaseServiceImpl implements FriendDatabaseService {
  public static final String ADD_FRIEND_STMT =
      "insert into friends(user_id, friend_id, status) values(?,?,?)";

  public static final String GET_FRIENDS_STMT =
      "select users.id, users.username, users.name, friends.status "
          + "from users "
          + "inner join friends on users.id = friends.friend_id "
          + "where friends.user_id = ? and friends.status != 'REMOVED'";

  public static final String GET_FRIENDS_WITH_MESSAGE_STMT =
      "select users.id, users.username, users.name, messages.message as last_message, "
          + "messages.sender_id as last_message_sender, messages.type as last_message_type, "
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
      "update friends set status='REMOVED' where (user_id=? and friend_id=?)";

  public static final String UPDATE_FRIEND_STMT =
      "update friends set status=? where user_id=? and friend_id=?";

  public static final String REMOVE_FRIEND_STMT =
      "update friends set status='REMOVED' where user_id=? and friend_id=?";

  public static final String CHECK_FRIEND_STATUS_STMT =
      "select status from friends where user_id=? and friend_id=?";

  public static final String STATUS_KEY = "STATUS";

  private SqlClient sqlClient;
  private DatabaseService dbService;

  @Inject
  public FriendDatabaseServiceImpl(DatabaseService databaseService) {
    sqlClient = databaseService.getPool();
    dbService = databaseService;
  }

  @Override
  public Future<Void> addFriend(long userId, long friendId) {
    log.debug("addFriend: userId={}, friendId={}", userId, friendId);

    Promise<Void> addFriendPromise = Promise.promise();

    TransactionManager transactionManager = dbService.getTransactionManager();

    transactionManager
        .begin()
        .compose(manager -> checkFriendStatus(userId, friendId, manager)) // check friend status
        .compose(manager -> internalAddFriend(userId, friendId, manager)) // update to database
        .compose(TransactionManager::commit)
        .onComplete(
            ar -> {
              transactionManager.close();
              if (ar.failed()) {
                log.error(
                    "Error in addFriend: userId={}, friendId={}", userId, friendId, ar.cause());
                addFriendPromise.fail(ar.cause());
              } else {
                addFriendPromise.complete();
              }
            });

    return addFriendPromise.future();
  }

  Future<TransactionManager> checkFriendStatus(
      long userId, long friendId, TransactionManager manager) {
    log.debug("checkFriendStatus: userId={}, friendId={}", userId, friendId);

    Promise<TransactionManager> promise = Promise.promise();

    manager
        .getTransaction()
        .preparedQuery(CHECK_FRIEND_STATUS_STMT)
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
                manager.set(STATUS_KEY, Friend.Status.NOTHING);
                promise.complete(manager);
                return;
              }

              // Aldready exist in table
              Row result = RowMapperUtil.firstRow(ar.result());

              // Check friend status
              if (result != null) {
                Friend.Status status = Friend.Status.valueOf(result.getString("status"));

                switch (status) {
                  case ACCEPTED:
                    promise.fail(
                        new AddFriendException(
                            "Aldready added", AddFriendException.ErrorCode.ACCEPTED));
                    break;
                  case WAITING:
                    promise.fail(
                        new AddFriendException("Waiting", AddFriendException.ErrorCode.WAITING));
                    break;
                  case NOT_ANSWER:
                    promise.fail(
                        new AddFriendException(
                            "Wait for accepted", AddFriendException.ErrorCode.NOT_ANSWER));
                    break;
                  default:
                    manager.set(STATUS_KEY, Friend.Status.REMOVED);
                    promise.complete(manager);
                    break;
                }
              }
            });

    return promise.future();
  }

  Future<TransactionManager> internalAddFriend(
      long userId, long friendId, TransactionManager manager) {
    log.debug("internalAddFriend, userId={}, friendId={}", userId, friendId);

    // Promise
    Promise<TransactionManager> promise = Promise.promise();

    // Create if not exist
    String stmt = ADD_FRIEND_STMT;
    List<Tuple> tuples =
        Arrays.asList(
            Tuple.of(userId, friendId, Friend.Status.WAITING.name()),
            Tuple.of(friendId, userId, Friend.Status.NOT_ANSWER.name()));

    // Update if existed
    if (manager.get(STATUS_KEY) == Friend.Status.REMOVED) {
      stmt = UPDATE_FRIEND_STMT;
      tuples =
          Arrays.asList(
              Tuple.of(Friend.Status.WAITING.name(), userId, friendId),
              Tuple.of(Friend.Status.NOT_ANSWER.name(), friendId, userId));
    }

    // Update/Create friend status
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

    // Future
    return promise.future();
  }

  @Override
  public Future<List<Friend>> getFriendList(long userId) {
    log.debug("getFriendList: userId={}", userId);

    Promise<List<Friend>> friendListPromise = Promise.promise();

    sqlClient
        .preparedQuery(GET_FRIENDS_STMT)
        .execute(
            Tuple.of(userId),
            rowSetAsyncRs -> {
              if (rowSetAsyncRs.failed()) {
                Throwable cause = rowSetAsyncRs.cause();
                log.error("Error in getFriendList: userId={}", userId, cause);
                friendListPromise.fail(cause);
                return;
              }

              RowSet<Row> rowSet = rowSetAsyncRs.result();
              List<Friend> userList = new ArrayList<>();
              for (Row row : rowSet) {
                userList.add(RowMapperUtil.mapRow(row, Friend.class));
              }
              friendListPromise.complete(userList);
            });

    return friendListPromise.future();
  }

  @Override
  public Future<Void> acceptFriend(long userId, long friendId) {
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
                      Arrays.asList(Tuple.of(userId, friendId), Tuple.of(friendId, userId)),
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
                log.debug("Accept friend successfully, userId={}, friendId={}", userId, friendId);
                statusPromise.complete();
              } else {
                log.debug("Error when add friend", ar.cause());
                statusPromise.fail(ar.cause());
              }
              transactionManager.close();
            });

    return statusPromise.future();
  }

  @Override
  public Future<Void> rejectFriend(long userId, long friendId) {
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
                      Arrays.asList(Tuple.of(userId, friendId), Tuple.of(friendId, userId)),
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
                log.debug("Reject friend successfully, userId={}, friendId={}", userId, friendId);
                statusPromise.complete();
              } else {
                log.debug(
                    "Error when reject friend, userId={}, friendId={}",
                    userId,
                    friendId,
                    ar.cause());
                transactionManager.close();
                statusPromise.fail(ar.cause());
              }
            });

    return statusPromise.future();
  }

  @Override
  public Future<List<Friend>> getChatFriendList(long userId) {
    Promise<List<Friend>> friendListPromise = Promise.promise();

    sqlClient
        .preparedQuery(GET_FRIENDS_WITH_MESSAGE_STMT)
        .execute(
            Tuple.of(userId),
            rowSetAsyncRs -> {
              if (rowSetAsyncRs.succeeded()) {
                RowSet<Row> rowSet = rowSetAsyncRs.result();
                List<Friend> userList = new ArrayList<>();
                for (Row row : rowSet) {
                  userList.add(RowMapperUtil.mapRow(row, Friend.class));
                }
                friendListPromise.complete(userList);
              } else {
                Throwable cause = rowSetAsyncRs.cause();
                log.error("Error when get friend list, userId={}", userId, cause);
                friendListPromise.fail(cause);
              }
            });

    return friendListPromise.future();
  }

  @Override
  public Future<Void> removeFriend(long userId, long friendId) {
    log.debug("removeFriend, userId={}, friendId={}", userId, friendId);

    Promise<Void> removeFriendPromise = Promise.promise();

    // Prepared query params
    List<Tuple> tuples = Arrays.asList(Tuple.of(userId, friendId), Tuple.of(friendId, userId));

    TransactionManager transactionManager = dbService.getTransactionManager();
    transactionManager
        .begin()
        .compose(
            manager -> {
              Promise<TransactionManager> promise = Promise.promise();

              manager
                  .getTransaction()
                  .preparedQuery(REMOVE_FRIEND_STMT)
                  .executeBatch(
                      tuples,
                      ar -> {
                        if (ar.failed()) {
                          promise.fail(ar.cause());
                          return;
                        }

                        promise.complete(manager);
                      });

              return promise.future();
            })
        .compose(TransactionManager::commit)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                log.debug("Remove friend successfully: userId={}, friendId={}", userId, friendId);
                removeFriendPromise.complete();
              } else {
                log.error(
                    "Error when remove friend: userId={}, friendId={}",
                    userId,
                    friendId,
                    ar.cause());
                removeFriendPromise.fail(ar.cause());
              }
            });

    return removeFriendPromise.future();
  }

  @Override
  public Future<Void> clearUnreadMessage(long userId, long friendId) {
    log.debug("clearUnreadMessage: userId={}, friendId={}", userId, friendId);

    Promise<Void> queryPromise = Promise.promise();

    sqlClient
        .preparedQuery(CLEAR_UNREAD_MESSAGE_STMT)
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
