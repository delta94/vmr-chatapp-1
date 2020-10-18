package com.anhvan.vmr.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;

public class AsyncUtil {
  public static <T> void convert(Promise<T> target, AsyncResult<T> source) {
    if (source.succeeded()) {
      target.complete(source.result());
    } else {
      target.fail(source.cause());
    }
  }
}
