package com.anhvan.vmr.util;

import io.vertx.core.Vertx;

import javax.inject.Inject;

public class AsyncWorkerUtil {
  private Vertx vertx;

  @Inject
  public AsyncWorkerUtil(Vertx vertx) {
    this.vertx = vertx;
  }

  public void execute(Runnable job) {
    vertx.executeBlocking(
        promise -> {
          job.run();
          promise.complete();
        },
        asyncResult -> {});
  }
}
