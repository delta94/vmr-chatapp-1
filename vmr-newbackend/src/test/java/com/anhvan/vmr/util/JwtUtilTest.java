package com.anhvan.vmr.util;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
public class JwtUtilTest {
  static JWTAuth jwtAuth;
  static AsyncWorkerUtil workerUtil;
  static JwtUtil jwtUtil;

  @BeforeAll
  static void setUp(Vertx vertx, VertxTestContext testContext) {
    jwtAuth = Mockito.mock(JWTAuth.class);
    workerUtil = new AsyncWorkerUtil(vertx);
    jwtUtil = new JwtUtil(workerUtil, jwtAuth);
    testContext.completeNow();
  }

  @Test
  void testGetTokenFromHeader() {
    HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
    Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer 123");
    RoutingContext routingContext = Mockito.mock(RoutingContext.class);
    Mockito.when(routingContext.request()).thenReturn(request);
    Assertions.assertEquals("123", jwtUtil.getTokenFromHeader(routingContext));
  }

  @Test
  void testGenerateToken(Vertx vertx, VertxTestContext testContext) {
    Mockito.when(jwtAuth.generateToken(ArgumentMatchers.any())).thenReturn("Sample token");
    Future<String> tokenFuture = jwtUtil.generate(1234);
    tokenFuture.onSuccess(
        token -> {
          Assertions.assertEquals("Sample token", token);
          testContext.completeNow();
        });
  }

  @Test
  void testVerifyToken(Vertx vertx, VertxTestContext testContext) {
    Mockito.doAnswer(invocationOnMock -> null)
        .when(jwtAuth)
        .authenticate(ArgumentMatchers.any(), ArgumentMatchers.any());
  }
}
