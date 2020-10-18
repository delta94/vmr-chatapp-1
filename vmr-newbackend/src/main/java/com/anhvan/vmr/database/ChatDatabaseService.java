package com.anhvan.vmr.database;

import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;

import java.util.List;

public interface ChatDatabaseService {
  Future<Long> addChat(Message message);

  Future<Long> addChat(Message message, SqlClient sqlClient);

  Future<List<Message>> getChatMessages(int user1, long user2, long offset);
}
