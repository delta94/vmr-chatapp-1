package com.anhvan.vmr.websocket;

import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
@Log4j2
public class WebSocketServiceTest {
  static JwtUtil jwtUtil;
  static WebSocketService webSocketService;

  @BeforeAll
  static void setUp() {
    jwtUtil = Mockito.mock(JwtUtil.class);
    webSocketService = new WebSocketService(jwtUtil);
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

  }
}
