package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.GrpcUserResponse;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Transaction;
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

  private MySQLPool pool;

  @Inject
  public FriendDatabaseServiceImpl(DatabaseService databaseService) {
    pool = databaseService.getPool();
  }

  @Override
  public Future<Long> addFriend(long userId, long friendId) {
    Promise<Long> addFriendIdPromise = Promise.promise();

    pool.begin(
        ar -> {
          if (ar.succeeded()) {
            // Start new transaction
            Transaction transaction = ar.result();

            // Add friend
            internalAddFriend(transaction, addFriendIdPromise, userId, friendId);
          } else {
            log.error("Error when start a transaction", ar.cause());
            addFriendIdPromise.fail(ar.cause());
          }
        });

    return addFriendIdPromise.future();
  }

  private void internalAddFriend(
      Transaction transaction, Promise<Long> addFriendPromise, long userId, long friendId) {
    // Prepared tuples
    List<Tuple> tuples =
        Arrays.asList(
            Tuple.of(userId, friendId, "WAITING"), Tuple.of(friendId, userId, "NOT_ANSWER"));

    transaction
        .preparedQuery(ADD_FRIEND_STMT)
        .executeBatch(
            tuples,
            queryAr -> {
              if (queryAr.succeeded()) {
                RowSet<Row> rs = queryAr.result();
                transaction.commit(
                    transactionAr -> {
                      if (transactionAr.succeeded()) {
                        addFriendPromise.complete(rs.property(MySQLClient.LAST_INSERTED_ID));
                      } else {
                        log.error("Error when add friend: commit error ", transactionAr.cause());
                      }
                    });
              } else {
                log.error(
                    "Error when add friend userId:{}, friendId:{}",
                    userId,
                    friendId,
                    queryAr.cause());
                addFriendPromise.fail(queryAr.cause());
              }
            });
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
  public Future<String> acceptFriend(long invitorId, long userId) {
    Promise<String> statusPromise = Promise.promise();

    pool.preparedQuery(ACCEPT_FRIEND)
        .executeBatch(
            Arrays.asList(Tuple.of(invitorId, userId), Tuple.of(userId, invitorId)),
            ar -> {
              if (ar.succeeded()) {
                statusPromise.complete("ACCEPTED");
              } else {
                Throwable cause = ar.cause();
                log.error("Error when accept friend request", cause);
                statusPromise.fail(cause);
              }
            });

    return statusPromise.future();
  }

  @Override
  public Future<String> rejectFriend(long invitorId, long userId) {
    Promise<String> statusPromise = Promise.promise();

    pool.preparedQuery("delete from friends where (user_id=? and friend_id=?)")
        .executeBatch(
            Arrays.asList(Tuple.of(userId, invitorId), Tuple.of(invitorId, userId)),
            ar -> {
              if (ar.succeeded()) {
                statusPromise.complete("OK");
              } else {
                Throwable cause = ar.cause();
                log.error("Error when remove friend", cause);
                statusPromise.fail(cause);
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
