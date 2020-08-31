package com.anhvan.vmr.database;

import com.anhvan.vmr.model.WsMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Tuple;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;

@Log4j2
public class ChatDBService {
  private MySQLPool pool;

  @Inject
  public ChatDBService(DatabaseService databaseService) {
    pool = databaseService.getPool();
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
}
