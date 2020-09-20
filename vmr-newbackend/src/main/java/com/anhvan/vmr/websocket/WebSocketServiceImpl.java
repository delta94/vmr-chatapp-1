package com.anhvan.vmr.websocket;

import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.Json;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketServiceImpl implements WebSocketService {
  private Map<Integer, Set<ServerWebSocket>> connections = new ConcurrentHashMap<>();
  private JwtUtil jwtUtil;

  @Inject
  public WebSocketServiceImpl(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public WebSocketServiceImpl(JwtUtil jwtUtil, Map<Integer, Set<ServerWebSocket>> connections) {
    this.jwtUtil = jwtUtil;
    this.connections = connections;
  }

  @Override
  public Future<Integer> authenticate(ServerWebSocket conn) {
    String token = conn.query().substring(6);
    return jwtUtil.authenticate(token);
  }

  @Override
  public void addConnection(int userId, ServerWebSocket serverWebSocket) {
    if (connections.containsKey(userId)) {
      connections.get(userId).add(serverWebSocket);
    } else {
      Set<ServerWebSocket> userConnections = new ConcurrentHashSet<>();
      userConnections.add(serverWebSocket);
      connections.put(userId, userConnections);
    }
  }

  @Override
  public void removeConnection(int userId, ServerWebSocket conn) {
    Set<ServerWebSocket> userConns = connections.get(userId);
    if (userConns != null) {
      userConns.remove(conn);
      if (userConns.size() == 0) {
        connections.remove(userId);
      }
    }
  }

  @Override
  public void sendTo(int userId, WebSocketMessage msg) {
    String msgString = Json.encode(msg);
    Set<ServerWebSocket> receiverConn = connections.get(userId);
    for (ServerWebSocket conn : receiverConn) {
      conn.writeTextMessage(msgString);
    }
  }

  @Override
  public void broadCast(WebSocketMessage msg) {
    String msgString = Json.encode(msg);
    for (Set<ServerWebSocket> wsSet : connections.values()) {
      for (ServerWebSocket ws : wsSet) {
        ws.writeTextMessage(msgString);
      }
    }
  }

  @Override
  public boolean checkOnline(int id) {
    return connections.containsKey(id);
  }

  @Override
  public Set<Integer> getOnlineIds() {
    return connections.keySet();
  }
}
