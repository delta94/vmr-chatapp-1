package com.anhvan.vmr.util;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class AsyncWorkerUtilTest {
  @Test
  void testAsyncWorkerUtil(Vertx vertx, VertxTestContext vertxTestContext) {
    AsyncWorkerUtil workerUtil = new AsyncWorkerUtil(vertx);
    workerUtil.execute(vertxTestContext::completeNow);
  }
}
