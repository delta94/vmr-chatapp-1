package com.anhvan.vmr.websocket;

import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.Json;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(VertxExtension.class)
@Log4j2
public class WebSocketServiceTest {
  JwtUtil jwtUtil;
  WebSocketService webSocketService;
  Map<Integer, Set<ServerWebSocket>> connections;

  @BeforeEach
  void setUp() {
    jwtUtil = Mockito.mock(JwtUtil.class);
    connections = Mockito.spy(new ConcurrentHashMap<>());
    webSocketService = new WebSocketService(jwtUtil, connections);
  }

  @Test
  void testAuthenticateSuccess(VertxTestContext testContext) {
    ServerWebSocket serverWebSocket = Mockito.mock(ServerWebSocket.class);
    Mockito.when(serverWebSocket.query()).thenReturn("token=123");
    Mockito.when(jwtUtil.authenticate("123")).thenReturn(Future.succeededFuture(123));
    webSocketService
        .authenticate(serverWebSocket)
        .onSuccess(
            id -> {
              Assertions.assertEquals(123, id);
              testContext.completeNow();
            });
  }

  @Test
  void testAuthenticateFails(VertxTestContext testContext) {
    ServerWebSocket serverWebSocket = Mockito.mock(ServerWebSocket.class);
    Mockito.when(serverWebSocket.query()).thenReturn("token=123");
    Mockito.when(jwtUtil.authenticate("123")).thenReturn(Future.failedFuture("Token not valid"));
    webSocketService
        .authenticate(serverWebSocket)
        .onFailure(
            failue -> {
              log.trace(failue);
              Assertions.assertTrue(failue instanceof NoStackTraceThrowable);
              testContext.completeNow();
            });
  }

  @Test
  void testAddConnection() {
    ServerWebSocket conn = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(1, conn);
    webSocketService.addConnection(1, conn);
    webSocketService.addConnection(2, conn);
    Mockito.verify(connections).put(ArgumentMatchers.eq(1), ArgumentMatchers.any());
    Mockito.verify(connections).put(ArgumentMatchers.eq(2), ArgumentMatchers.any());
  }

  @Test
  void testRemoveConnection() {
    ServerWebSocket conn = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(1, conn);

    // Remove two times
    webSocketService.removeConnection(1, conn);
    webSocketService.removeConnection(1, conn);

    // Internal remove only one time
    Mockito.verify(connections).remove(1);
  }

  @Test
  void testSendMessageTo() {
    ServerWebSocket conn = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(1, conn);
    ServerWebSocket conn2 = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(1, conn2);
    ServerWebSocket conn3 = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(2, conn3);

    WebSocketMessage message = WebSocketMessage.builder().data("Hello world").build();
    webSocketService.sendTo(1, message);

    Mockito.verify(conn).writeTextMessage(Json.encode(message));
    Mockito.verify(conn2).writeTextMessage(Json.encode(message));
    Mockito.verify(conn3, Mockito.never()).writeTextMessage(ArgumentMatchers.anyString());
  }

  @Test
  void testBroadcastMessage() {
    ServerWebSocket conn = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(1, conn);
    ServerWebSocket conn2 = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(1, conn2);
    ServerWebSocket conn3 = Mockito.mock(ServerWebSocket.class);
    webSocketService.addConnection(2, conn3);

    WebSocketMessage message = WebSocketMessage.builder().data("Hello world").build();
    webSocketService.broadCast(message);

    Mockito.verify(conn).writeTextMessage(Json.encode(message));
    Mockito.verify(conn2).writeTextMessage(Json.encode(message));
    Mockito.verify(conn3, Mockito.times(1)).writeTextMessage(ArgumentMatchers.anyString());
  }
}
