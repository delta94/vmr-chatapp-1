package com.anhvan.vmr.database;

import com.anhvan.vmr.model.WsMessage;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ChatDBService {
  private MySQLPool pool;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public ChatDBService(DatabaseService databaseService, AsyncWorkerUtil workerUtil) {
    pool = databaseService.getPool();
    this.workerUtil = workerUtil;
  }

  public Future<WsMessage> addChat(WsMessage msg) {
    Promise<WsMessage> chatMessagePromise = Promise.promise();
    pool.preparedQuery(
            "insert into messages (sender, receiver, message, send_time) values (?,?,?,?)")
        .execute(
            Tuple.of(
                msg.getSenderId(),
                msg.getReceiverId(),
                msg.getMessage(),
                Timestamp.from(Instant.ofEpochSecond(msg.getTimestamp()))),
            rowSetAsyncResult -> {
              if (rowSetAsyncResult.succeeded()) {
                chatMessagePromise.complete(msg.toBuilder().build());
              } else {
                log.error("Error when add chat", rowSetAsyncResult.cause());
              }
            });
    return chatMessagePromise.future();
  }

  public Future<List<WsMessage>> getChatMessages(int user1, int user2, int limit, int offset) {
    log.trace("get chat from db");
    Promise<List<WsMessage>> listMsgPromise = Promise.promise();
    List<WsMessage> messages = new ArrayList<>();
    workerUtil.execute(
        () ->
            pool.preparedQuery(
                    "select * from messages where (sender=? and receiver=?) or (sender=? and receiver=?) "
                        + "order by send_time desc")
                .execute(
                    Tuple.of(user1, user2, user2, user1),
                    rowSet -> {
                      if (rowSet.succeeded()) {
                        RowSet<Row> result = rowSet.result();

                        log.trace("Execute query 0");
                        log.trace(result.size());
                        result.forEach(row -> messages.add(rowToWsMessage(row)));
                        log.trace("Execute query 1");
                      } else {
                        log.error("Error when get messages", rowSet.cause());
                      }
                      listMsgPromise.complete(messages);
                    }));
    return listMsgPromise.future();
  }

  public WsMessage rowToWsMessage(Row row) {
    long timeStamp = row.getLocalDateTime("send_time").atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toEpochSecond();
    return WsMessage.builder()
        .senderId(row.getInteger("sender"))
        .receiverId(row.getInteger("receiver"))
        .timestamp(timeStamp)
        .build();
  }
}
