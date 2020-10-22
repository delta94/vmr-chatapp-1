package com.anhvan.vmr.database;

import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.util.RowMapperUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
@Builder
public class ChatDatabaseServiceImpl implements ChatDatabaseService {
  public static final String GET_MESSAGE_STMT =
      "select * from  "
          + "(select * from messages where sender_id=? and receiver_id=? "
          + "union select * from messages where sender_id=? and receiver_id=?) msgs "
          + "order by id desc "
          + "limit ?, 20";

  public static final String INSERT_MESSAGE_STMT =
      "insert into messages (sender_id, receiver_id, message, send_time, type) values (?, ?, ?, ?, ?)";

  public static final String UPDATE_LAST_MESSAGE_STMT =
      "update friends set last_message_id=? where " + "user_id=? and friend_id=?";

  public static final String INCREASE_UNREAD_MSG_STMT =
      "update friends set num_unread_message=num_unread_message+1 "
          + "where user_id=? and friend_id=?";

  private SqlClient sqlClient;

  public ChatDatabaseServiceImpl(SqlClient sqlClient) {
    this.sqlClient = sqlClient;
  }

  @Override
  public Future<Long> addChat(Message message) {
    return addChat(message, sqlClient);
  }

  @Override
  public Future<Long> addChat(Message message, SqlClient sqlClient) {
    log.debug("addChat: message={}", message);

    Promise<Long> idPromise = Promise.promise();

    // Extract info
    long senderId = message.getSenderId();
    long receiverId = message.getReceiverId();

    // Add friend, update last message, update number of unread message
    internalAddChat(message, sqlClient)
        .compose(insertedId -> updateLastMessageId(senderId, receiverId, insertedId, sqlClient))
        .compose(id -> increaseUnreadMessage(senderId, receiverId, id, sqlClient))
        .onComplete(
            ar -> {
              if (ar.failed()) {
                log.error("Error when add chat, message={}", message, ar.cause());
                idPromise.fail(ar.cause());
                return;
              }

              idPromise.complete(ar.result());
            });

    return idPromise.future();
  }

  private Future<Long> internalAddChat(Message message, SqlClient sqlClient) {
    log.debug("internalAddChat: message={}", message);

    Promise<Long> idPromise = Promise.promise();

    String type = message.getType();
    if (type == null) {
      type = Message.Type.CHAT.name();
    }

    sqlClient
        .preparedQuery(INSERT_MESSAGE_STMT)
        .execute(
            Tuple.of(
                message.getSenderId(),
                message.getReceiverId(),
                message.getMessage(),
                message.getTimestamp(),
                type),
            rs -> {
              if (!rs.succeeded()) {
                log.error("Error when add chat, message={}", message, rs.cause());
                idPromise.fail(rs.cause());
                return;
              }
              idPromise.complete(rs.result().property(MySQLClient.LAST_INSERTED_ID));
            });

    return idPromise.future();
  }

  private Future<Long> updateLastMessageId(
      long userId, long friendId, long messageId, SqlClient sqlClient) {
    log.debug(
        "updateLastMessageId, userId={}, friendId={}, messageId={}", userId, friendId, messageId);

    Promise<Long> updatedPromise = Promise.promise();

    sqlClient
        .preparedQuery(UPDATE_LAST_MESSAGE_STMT)
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

  private Future<Long> increaseUnreadMessage(
      long senderId, long receiverId, long messageId, SqlClient sqlClient) {
    log.debug("increaseUnreadMessage, userId={}, friendId={}", senderId, receiverId);

    Promise<Long> updatedPromise = Promise.promise();

    sqlClient
        .preparedQuery(INCREASE_UNREAD_MSG_STMT)
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

  public Future<List<Message>> getChatMessages(int user1, long user2, long offset) {
    log.debug("getChatMessages: user1Id={}, user2Id={}, offset={}", user1, user2, offset);

    Promise<List<Message>> listMsgPromise = Promise.promise();

    sqlClient
        .preparedQuery(GET_MESSAGE_STMT)
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

  private Message rowToMessage(Row row) {
    return RowMapperUtil.mapRow(row, Message.class);
  }
}
