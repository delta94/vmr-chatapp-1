package com.anhvan.vmr.api.http;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class RegisterHandler implements HttpApiHandler {
  private final Router router;
  private final Vertx vertx;

  public RegisterHandler(Vertx vertx) {
    router = Router.router(vertx);
    this.vertx = vertx;
  }

  @Override
  public Router getRouter() {
    router.post("/").handler(this::registerHandler);
    return router;
  }

  private void registerHandler(RoutingContext routingContext) {
    HttpServerRequest request = routingContext.request();
    request.bodyHandler(
        buffer -> {
          System.out.println(buffer.toJsonObject());
        });
    routingContext.response().end("END");
  }
}
