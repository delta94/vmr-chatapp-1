package com.anhvan.vmr.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class BaseHandler implements Handler<RoutingContext> {
  public void handle(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    response.putHeader("Content-Type", "application/json");
    handle(routingContext.request(), response, routingContext.getBodyAsJson());
  }

  abstract void handle(HttpServerRequest request, HttpServerResponse response, JsonObject body);
}
