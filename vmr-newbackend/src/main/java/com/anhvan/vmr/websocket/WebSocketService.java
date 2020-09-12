package com.anhvan.vmr.websocket;

import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.Json;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
  private Map<Integer, Set<ServerWebSocket>> connections = new ConcurrentHashMap<>();
  private JwtUtil jwtUtil;

  @Inject
  public WebSocketService(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public Future<Integer> authenticate(ServerWebSocket conn) {
    String token = conn.query().substring(6);
    return jwtUtil.authenticate(token);
  }

  public void addConnection(int userId, ServerWebSocket serverWebSocket) {
    if (connections.containsKey(userId)) {
      connections.get(userId).add(serverWebSocket);
    } else {
      Set<ServerWebSocket> userConnections = new ConcurrentHashSet<>();
      userConnections.add(serverWebSocket);
      connections.put(userId, userConnections);
    }
  }

  public void removeConnection(int userId, ServerWebSocket conn) {
    Set<ServerWebSocket> userConns = connections.get(userId);
    userConns.remove(conn);
    if (userConns.size() == 0) {
      connections.remove(userId);
    }
  }

  public void sendTo(int userId, Message msg) {
    String msgString = Json.encode(msg);
    Set<ServerWebSocket> receiverConn = connections.get(userId);
    for (ServerWebSocket conn : receiverConn) {
      conn.writeTextMessage(msgString);
    }
  }

  public void broadCast(Message msg) {
    String msgString = Json.encode(msg);
    for (Set<ServerWebSocket> wsSet : connections.values()) {
      for (ServerWebSocket ws : wsSet) {
        ws.writeTextMessage(msgString);
      }
    }
  }

  public boolean checkOnline(int id) {
    return connections.containsKey(id);
  }

  public Set<Integer> getOnlineIds() {
    return connections.keySet();
  }
}
