package com.anhvan.vmr.util;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

@ExtendWith(VertxExtension.class)
public class JwtUtilTest {
  @Test
  void testAsyncWorkerUtil(Vertx vertx, VertxTestContext vertxTestContext) {
    JWTAuth jwtAuth = Mockito.mock(JWTAuth.class);
    AsyncWorkerUtil workerUtil = Mockito.mock(AsyncWorkerUtil.class);

    JwtUtil jwtUtil = new JwtUtil(workerUtil, jwtAuth);
  }
}
