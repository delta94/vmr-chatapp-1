package com.anhvan.vmr.util;

import io.vertx.core.Vertx;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class AsyncWorkerUtil {
  private Vertx vertx;

  @Inject
  public AsyncWorkerUtil(Vertx vertx) {
    this.vertx = vertx;
  }

  public void execute(@NonNull Runnable job) {
    vertx.executeBlocking(
        promise -> {
          try {
            job.run();
          } catch (Throwable e) {
            log.error("Uncached exception occur in worker thread", e);
          }
          promise.complete();
        },
        asyncResult -> {});
  }
}
