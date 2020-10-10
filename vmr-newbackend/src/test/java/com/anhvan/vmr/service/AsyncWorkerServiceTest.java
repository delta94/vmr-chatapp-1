package com.anhvan.vmr.service;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class AsyncWorkerServiceTest {
  @Test
  void testAsyncWorkerUtil(Vertx vertx, VertxTestContext vertxTestContext) {
    AsyncWorkerService workerUtil = new AsyncWorkerService(vertx);
    workerUtil.execute(vertxTestContext::completeNow);
  }
}
