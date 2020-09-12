package com.anhvan.vmr.util;

import io.vertx.core.Vertx;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AsyncWorkerUtil {
  private Vertx vertx;

  @Inject
  public AsyncWorkerUtil(Vertx vertx) {
    this.vertx = vertx;
  }

  public void execute(@NonNull Runnable job) {
    vertx.executeBlocking(
        promise -> {
          job.run();
          promise.complete();
        },
        asyncResult -> {});
  }
}
