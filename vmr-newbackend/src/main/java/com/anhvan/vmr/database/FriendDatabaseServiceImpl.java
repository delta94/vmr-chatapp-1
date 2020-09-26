package com.anhvan.vmr.database;

import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FriendDatabaseServiceImpl implements FriendDatabaseService {
  private static final String ADD_FRIEND_QUERY =
      "insert into friends(user_id, friend_id, status) values(?,?,'WAITING')";

  private static final String GET_LIST_FRIEND_QUERY =
      "select user.id, user.username, user.name from users user inner join friends friend"
          + "on user.id = friend.friend_id"
          + "where friend.user_id = ? and status = 'ACCEPTED'";

  private static final String GET_FRIEND_INVITATION_QUERY =
      "select user.id, user.username, user.name from users user inner join friends friend"
          + "on user.id = friend.user_id"
          + "where friend.friend_id = ? and status = 'WAITING'";

  private MySQLPool pool;

  @Inject
  public FriendDatabaseServiceImpl(DatabaseService databaseService) {
    pool = databaseService.getPool();
  }

  @Override
  public Future<Long> addFriend(long userId, long friendId) {
    Promise<Long> addFriendIdPromise = Promise.promise();

    pool.preparedQuery(ADD_FRIEND_QUERY)
        .execute(
            Tuple.of(userId, friendId),
            rowSetAsyncResult -> {
              if (rowSetAsyncResult.succeeded()) {
                RowSet<Row> rs = rowSetAsyncResult.result();
                addFriendIdPromise.complete(rs.property(MySQLClient.LAST_INSERTED_ID));
              } else {
                log.error(
                    "Error when add friend userId:{}, friendId:{}",
                    userId,
                    friendId,
                    rowSetAsyncResult.cause());
                addFriendIdPromise.fail(rowSetAsyncResult.cause());
              }
            });

    return addFriendIdPromise.future();
  }

  @Override
  public Future<List<User>> getFriendList(long userId) {
    Promise<List<User>> friendListPromise = Promise.promise();

    pool.preparedQuery(GET_LIST_FRIEND_QUERY)
        .execute(
            Tuple.of(userId),
            rowSetAsyncRs -> {
              if (rowSetAsyncRs.succeeded()) {
                RowSet<Row> rowSet = rowSetAsyncRs.result();
                List<User> userList = new ArrayList<>();
                for (Row row : rowSet) {
                  userList.add(RowMapperUtil.mapRow(row, User.class));
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
  public Future<List<User>> getFriendInvitation(long userId) {
    Promise<List<User>> invitationPromise = Promise.promise();

    pool.preparedQuery(GET_FRIEND_INVITATION_QUERY)
        .execute(
            Tuple.of(userId),
            rowSetAsyncRs -> {
              if (rowSetAsyncRs.succeeded()) {
                RowSet<Row> rowSet = rowSetAsyncRs.result();
                List<User> userList = new ArrayList<>();
                for (Row row : rowSet) {
                  userList.add(RowMapperUtil.mapRow(row, User.class));
                }
                invitationPromise.complete(userList);
              } else {
                Throwable cause = rowSetAsyncRs.cause();
                log.error("Error when get friend list of user {}", userId, cause);
                invitationPromise.fail(cause);
              }
            });

    return invitationPromise.future();
  }

  @Override
  public Future<String> acceptFriend(long invitorId, long userId) {
    Promise<String> statusPromise = Promise.promise();

    pool.getConnection(
        ar -> {
          if (ar.succeeded()) {
            SqlConnection conn = ar.result();
            Transaction transaction = conn.begin();

            conn.preparedQuery(
                    "update friends set status='ACCEPTED' where user_id=? and friend_id=?")
                .execute(
                    Tuple.of(invitorId, userId),
                    updateAr -> {
                      if (updateAr.succeeded()) {
                        conn.preparedQuery(
                                "insert into friends (user_id, friend_id, status) values (?, ?, 'ACCEPTED')")
                            .execute(
                                Tuple.of(userId, invitorId),
                                insertRs -> {
                                  if (insertRs.succeeded()) {
                                    transaction.commit(
                                        commitAr -> {
                                          if (commitAr.succeeded()) {
                                            statusPromise.complete();
                                          } else {
                                            log.error(
                                                "Fail when commit transaction", commitAr.cause());
                                            statusPromise.fail(commitAr.cause());
                                          }
                                          conn.close();
                                        });

                                  } else {
                                    log.error("Error when accept friend", updateAr.cause());
                                    conn.close();
                                  }
                                });

                      } else {
                        log.error("Error when accept friend", updateAr.cause());
                        statusPromise.fail(updateAr.cause());
                        conn.close();
                      }
                    });
          }
        });

    return statusPromise.future();
  }
}
