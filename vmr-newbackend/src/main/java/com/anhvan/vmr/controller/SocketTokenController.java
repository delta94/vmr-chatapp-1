package com.anhvan.vmr.controller;

import com.anhvan.vmr.util.ControllerUtil;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SocketTokenController implements Controller {
  private JwtUtil jwtUtil;
  private Vertx vertx;

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router.get("/").handler(this::handleGenToken);
    return router;
  }

  public void handleGenToken(RoutingContext routingContext) {
    int userId = routingContext.user().principal().getInteger("userId");
    jwtUtil
        .generate(userId, 90)
        .onSuccess(
            token ->
                ControllerUtil.jsonResponse(
                    routingContext.response(), new JsonObject().put("token", token)));
  }
}
