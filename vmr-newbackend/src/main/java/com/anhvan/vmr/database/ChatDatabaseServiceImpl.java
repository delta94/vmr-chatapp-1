package com.anhvan.vmr.database;

import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
public class ChatDatabaseServiceImpl implements ChatDatabaseService {
  public static final String GET_MESSAGE_STMT =
      "select * from  "
          + "(select * from messages where sender=? and receiver=? "
          + "union select * from messages where sender=? and receiver=?) msgs "
          + "order by id desc "
          + "limit ?, 20";

  public static final String INSERT_MESSAGE_STMT =
      "insert into messages (sender, receiver, message, send_time, type) values (?, ?, ?, ?, ?)";

  public static final String UPDATE_LAST_MESSAGE_STMT =
      "update friends set last_message_id=? where " + "user_id=? and friend_id=?";

  public static final String INCREASE_UNREAD_MSG_STMT =
      "update friends set num_unread_message=num_unread_message+1 "
          + "where user_id=? and friend_id=?";

  private MySQLPool pool;

  @Inject
  public ChatDatabaseServiceImpl(DatabaseService databaseService) {
    pool = databaseService.getPool();
  }

  @Override
  public Future<Long> addChat(Message msg) {
    Promise<Long> idPromise = Promise.promise();

    long senderId = msg.getSenderId();
    long receiverId = msg.getReceiverId();

    internalAddChat(msg)
        .compose(insertedId -> updateLastMessageId(senderId, receiverId, insertedId))
        .compose(id -> increaseUnreadMessage(senderId, receiverId, id))
        .onComplete(
            ar -> {
              if (ar.failed()) {
                log.error("Error when add chat, message={}", msg, ar.cause());
                idPromise.fail(ar.cause());
                return;
              }

              idPromise.complete(ar.result());
            });

    return idPromise.future();
  }

  private Future<Long> internalAddChat(Message msg) {
    Promise<Long> idPromise = Promise.promise();

    String type = msg.getType();
    if (type == null) {
      type = Message.Type.CHAT.name();
    }

    pool.preparedQuery(INSERT_MESSAGE_STMT)
        .execute(
            Tuple.of(
                msg.getSenderId(), msg.getReceiverId(), msg.getMessage(), msg.getTimestamp(), type),
            rs -> {
              if (!rs.succeeded()) {
                log.error("Error when add chat, message={}", msg, rs.cause());
                idPromise.fail(rs.cause());
                return;
              }
              idPromise.complete(rs.result().property(MySQLClient.LAST_INSERTED_ID));
            });

    return idPromise.future();
  }

  private Future<Long> updateLastMessageId(long userId, long friendId, long messageId) {
    Promise<Long> updatedPromise = Promise.promise();

    pool.preparedQuery(UPDATE_LAST_MESSAGE_STMT)
        .executeBatch(
            Arrays.asList(
                Tuple.of(messageId, userId, friendId), Tuple.of(messageId, friendId, userId)),
            ar -> {
              if (ar.succeeded()) {
                updatedPromise.complete(userId);
              } else {
                log.error(
                    "Error when update last message: user1={}, user2={}, last message id ={}",
                    userId,
                    friendId,
                    messageId,
                    ar.cause());
                updatedPromise.fail(ar.cause());
              }
            });

    return updatedPromise.future();
  }

  private Future<Long> increaseUnreadMessage(long senderId, long receiverId, long messageId) {
    Promise<Long> updatedPromise = Promise.promise();

    pool.preparedQuery(INCREASE_UNREAD_MSG_STMT)
        .execute(
            Tuple.of(receiverId, senderId),
            ar -> {
              if (ar.failed()) {
                log.error(
                    "Error when update number of unread message userId={}, friendId={}",
                    receiverId,
                    senderId,
                    ar.cause());
                updatedPromise.fail(ar.cause());
                return;
              }

              updatedPromise.complete(messageId);
            });

    return updatedPromise.future();
  }

  public Future<List<Message>> getChatMessages(int user1, int user2, int offset) {
    log.debug("Get chat message: user1={}, user2={}, offset={}", user1, user2, offset);

    Promise<List<Message>> listMsgPromise = Promise.promise();

    pool.preparedQuery(GET_MESSAGE_STMT)
        .execute(
            Tuple.of(user1, user2, user2, user1, offset),
            rowSet -> {
              List<Message> messages = new ArrayList<>();

              if (rowSet.succeeded()) {
                RowSet<Row> result = rowSet.result();
                for (Row row : result) {
                  messages.add(rowToMessage(row));
                }
              } else {
                log.error(
                    "Error when get chat message: user1={}, user2={}, offset={}",
                    user1,
                    user2,
                    offset,
                    rowSet.cause());
              }

              Collections.reverse(messages);
              listMsgPromise.complete(messages);
            });

    return listMsgPromise.future();
  }

  public Message rowToMessage(Row row) {
    return RowMapperUtil.mapRow(row, Message.class);
  }
}
