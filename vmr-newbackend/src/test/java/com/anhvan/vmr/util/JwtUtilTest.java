package com.anhvan.vmr.util;

import com.anhvan.vmr.config.AuthConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.impl.JWTUser;
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
  static AuthConfig config;

  @BeforeAll
  static void setUp(VertxTestContext testContext) {
    jwtAuth = Mockito.mock(JWTAuth.class);
    workerUtil = Mockito.mock(AsyncWorkerUtil.class);
    config = Mockito.mock(AuthConfig.class);
    jwtUtil = new JwtUtil(workerUtil, jwtAuth, config);

    // Fake worker util object
    Mockito.doAnswer(
            invocationOnMock -> {
              Runnable r = invocationOnMock.getArgument(0);
              r.run();
              return null;
            })
        .when(workerUtil)
        .execute(ArgumentMatchers.any(Runnable.class));

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
  void testGenerateToken(VertxTestContext testContext) {
    Mockito.when(jwtAuth.generateToken(ArgumentMatchers.any())).thenReturn("Sample token");
    Future<String> tokenFuture = jwtUtil.generate(1234);
    tokenFuture.onSuccess(
        token -> {
          Assertions.assertEquals("Sample token", token);
          testContext.completeNow();
        });
    tokenFuture.onFailure(testContext::failNow);
  }

  @Test
  void testVerifyToken(VertxTestContext testContext) {
    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<User>> handler = invocationOnMock.getArgument(1);
              handler.handle(
                  Future.succeededFuture(
                      new JWTUser(new JsonObject().put("userId", 1234), "1234")));
              return null;
            })
        .when(jwtAuth)
        .authenticate(ArgumentMatchers.any(), ArgumentMatchers.any());

    jwtUtil
        .authenticate("Sample token")
        .onComplete(
            integerAsyncResult -> {
              if (integerAsyncResult.succeeded()) {
                Assertions.assertEquals(1234, integerAsyncResult.result());
                testContext.completeNow();
              } else {
                testContext.failNow(integerAsyncResult.cause());
              }
            });
  }

  @Test
  void testVerifyTokenFailed(VertxTestContext testContext) {
    Mockito.doAnswer(
            invocationOnMock -> {
              Handler<AsyncResult<User>> handler = invocationOnMock.getArgument(1);
              handler.handle(Future.failedFuture("Test"));
              return null;
            })
        .when(jwtAuth)
        .authenticate(ArgumentMatchers.any(), ArgumentMatchers.any());

    jwtUtil
        .authenticate("Sample token")
        .onComplete(
            integerAsyncResult -> {
              Assertions.assertTrue(integerAsyncResult.failed());
              testContext.completeNow();
            });
  }
}
