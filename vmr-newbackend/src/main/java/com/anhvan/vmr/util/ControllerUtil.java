package com.anhvan.vmr.util;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public class ControllerUtil {
  public static void jsonResponse(HttpServerResponse response, JsonObject json) {
    response.putHeader("Content-Type", "application/json; charset=utf-8");
    response.end(json.toBuffer());
  }

  public static void jsonResponse(HttpServerResponse response, JsonObject json, int status) {
    response.setStatusCode(status);
    jsonResponse(response, json);
  }
}
