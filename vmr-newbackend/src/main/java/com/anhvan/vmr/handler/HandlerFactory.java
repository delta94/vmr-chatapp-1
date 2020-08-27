package com.anhvan.vmr.handler;

import io.vertx.ext.web.Router;

public class HandlerFactory {
  public static void init(Router router) {
    router.get("/").handler(new IndexHandler());
  }
}
