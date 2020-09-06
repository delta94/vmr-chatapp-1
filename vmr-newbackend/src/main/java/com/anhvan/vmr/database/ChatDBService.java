package com.anhvan.vmr.database;

import com.anhvan.vmr.model.WsMessage;
import io.vertx.core.Future;

import java.util.List;

public interface ChatDBService {
  void addChat(WsMessage message);

  Future<List<WsMessage>> getChatMessages(int user1, int user2, int offset);
}
