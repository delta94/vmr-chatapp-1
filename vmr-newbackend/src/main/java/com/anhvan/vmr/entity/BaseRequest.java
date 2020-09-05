package com.anhvan.vmr.entity;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BaseRequest {
  private MultiMap params;
  private MultiMap queries;
  private JsonObject principal;
  private JsonObject body;
  private HttpServerRequest request;
  private RoutingContext routingContext;
}
