package com.anhvan.vmr.websocket;

import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;

import java.util.Set;

public interface WebSocketService {
  Future<Long> authenticate(ServerWebSocket conn);

  void addConnection(long userId, ServerWebSocket serverWebSocket);

  void removeConnection(long userId, ServerWebSocket conn);

  void sendTo(long userId, WebSocketMessage msg);

  void sendChatMessage(long sender, long receiver, Message message);

  void broadCast(WebSocketMessage msg);

  boolean checkOnline(long id);

  Set<Long> getOnlineIds();
}
