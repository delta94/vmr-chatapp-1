package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.util.ControllerUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
public class MessageListController implements Controller {
  private ChatCacheService chatCacheService;
  private ChatDBService chatDBService;
  private Vertx vertx;

  @Override
  public Router getRouter() {
    log.trace("Mount router");
    Router router = Router.router(vertx);
    router.get("/:friendId").handler(this::getChatMessages);
    return router;
  }

  public void getChatMessages(RoutingContext routingContext) {
    log.trace("Get request");
    HttpServerResponse response = routingContext.response();
    int userId = routingContext.user().principal().getInteger("userId");
    int friendId = Integer.parseInt(routingContext.request().getParam("friendId"));
    chatDBService
        .getChatMessages(userId, friendId, 0, 0)
        .onComplete(
            result -> {
              if (result.succeeded()) {
                ControllerUtil.jsonResponse(
                    response, new JsonObject().put("messages", result.result()));
              } else {
                log.error("Error when get chat messages", result.cause());
                response.setStatusCode(500).end();
              }
            });
  }
}
