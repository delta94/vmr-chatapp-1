package com.anhvan.vmr.handler;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public class IndexHandler extends BaseHandler {
  @Override
  void handle(HttpServerRequest request, HttpServerResponse response, JsonObject body) {
    response.end(new JsonObject().put("msg", "Hello world").toBuffer());
  }
}
