package com.anhvan.vmr.database;

import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;

import java.util.List;

public interface ChatDatabaseService {
  Future<Long> addChat(Message message);

  Future<List<Message>> getChatMessages(int user1, int user2, int offset);

  Future<Message> getLastMessage(long user1, long user2);
}
