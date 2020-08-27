package com.anhvan.vmr.controller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import javax.inject.Inject;

public class IndexController implements Controller {
  private Vertx vertx;

  @Inject
  public IndexController(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router.get("/").handler(routingContext -> routingContext.response().end("Hello world"));
    return router;
  }
}
