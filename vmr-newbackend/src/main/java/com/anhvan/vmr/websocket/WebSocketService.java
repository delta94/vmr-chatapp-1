package com.anhvan.vmr.websocket;

import com.anhvan.vmr.entity.WebSocketMessage;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;

import java.util.Set;

public interface WebSocketService {
  Future<Integer> authenticate(ServerWebSocket conn);

  void addConnection(int userId, ServerWebSocket serverWebSocket);

  void removeConnection(int userId, ServerWebSocket conn);

  void sendTo(int userId, WebSocketMessage msg);

  void broadCast(WebSocketMessage msg);

  boolean checkOnline(int id);

  Set<Integer> getOnlineIds();
}
