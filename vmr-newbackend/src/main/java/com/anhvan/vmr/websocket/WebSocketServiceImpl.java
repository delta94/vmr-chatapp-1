package com.anhvan.vmr.websocket;

import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.Json;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Singleton
public class WebSocketServiceImpl implements WebSocketService {
  private Map<Long, Set<ServerWebSocket>> connections = new ConcurrentHashMap<>();
  private JwtUtil jwtUtil;

  @Inject
  public WebSocketServiceImpl(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public WebSocketServiceImpl(JwtUtil jwtUtil, Map<Long, Set<ServerWebSocket>> connections) {
    this.jwtUtil = jwtUtil;
    this.connections = connections;
  }

  @Override
  public Future<Long> authenticate(ServerWebSocket conn) {
    String token = conn.query().substring(6);
    return jwtUtil.authenticate(token);
  }

  @Override
  public void addConnection(long userId, ServerWebSocket serverWebSocket) {
    if (connections.containsKey(userId)) {
      connections.get(userId).add(serverWebSocket);
    } else {
      Set<ServerWebSocket> userConnections = new ConcurrentHashSet<>();
      userConnections.add(serverWebSocket);
      connections.put(userId, userConnections);
    }
    log.debug("Connections set {}", connections.keySet());
  }

  @Override
  public void removeConnection(long userId, ServerWebSocket conn) {
    Set<ServerWebSocket> userConns = connections.get(userId);
    if (userConns != null) {
      userConns.remove(conn);
      if (userConns.size() == 0) {
        connections.remove(userId);
      }
    }
    log.debug("Connections set when remove {}", connections.keySet());
  }

  @Override
  public void sendTo(long userId, WebSocketMessage msg) {
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
  public boolean checkOnline(long id) {
    return connections.containsKey(id);
  }

  @Override
  public Set<Long> getOnlineIds() {
    log.debug("Connections set {}", connections.keySet());
    return connections.keySet();
  }
}
