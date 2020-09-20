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
import java.util.Collections;
import java.util.List;

@Log4j2
public class ChatDatabaseServiceImpl implements ChatDatabaseService {
  public static final String GET_MESSAGES_QUERY =
      "select * from  "
          + "(select * from messages where sender=? and receiver=? "
          + "union select * from messages where sender=? and receiver=?) msgs "
          + "order by id desc "
          + "limit ?, 20";

  public static final String INSERT_MESSAGE =
      "insert into messages (sender, receiver, message, send_time) values (?, ?, ?, ?)";

  private MySQLPool pool;

  @Inject
  public ChatDatabaseServiceImpl(DatabaseService databaseService) {
    pool = databaseService.getPool();
  }

  public Future<Long> addChat(Message msg) {
    log.debug("Add chat message {} to database", msg);

    Promise<Long> idPromise = Promise.promise();

    pool.preparedQuery(INSERT_MESSAGE)
        .execute(
            Tuple.of(msg.getSenderId(), msg.getReceiverId(), msg.getMessage(), msg.getTimestamp()),
            rs -> {
              if (!rs.succeeded()) {
                log.error("Error when add chat {}", msg.toString(), rs.cause());
                idPromise.fail(rs.cause());
                return;
              }
              idPromise.complete(rs.result().property(MySQLClient.LAST_INSERTED_ID));
            });

    return idPromise.future();
  }

  public Future<List<Message>> getChatMessages(int user1, int user2, int offset) {
    log.debug(
        "Get chat message between user {} and {} from database with offset {}",
        user1,
        user2,
        offset);

    Promise<List<Message>> listMsgPromise = Promise.promise();

    List<Message> messages = new ArrayList<>();
    pool.preparedQuery(GET_MESSAGES_QUERY)
        .execute(
            Tuple.of(user1, user2, user2, user1, offset),
            rowSet -> {
              if (rowSet.succeeded()) {
                RowSet<Row> result = rowSet.result();
                for (Row row : result) {
                  messages.add(rowToMessage(row));
                }
              } else {
                log.error(
                    "Get chat message between user {} and {} from database with offset {}",
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
