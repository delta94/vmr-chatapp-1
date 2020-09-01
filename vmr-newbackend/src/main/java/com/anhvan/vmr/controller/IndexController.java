package com.anhvan.vmr.controller;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class IndexController implements Controller {
  private Vertx vertx;

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router
        .get("/")
        .handler(
            routingContext -> {
              HttpServerResponse response = routingContext.response();
              response.putHeader("Content-Type", "text/html; charset=utf-8");
              response.end("Hello world");
            });
    return router;
  }
}
