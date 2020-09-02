package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.model.WsMessage;
import com.anhvan.vmr.util.ControllerUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Log4j2
public class MessageListController implements Controller {
  private ChatCacheService chatCacheService;
  private ChatDBService chatDBService;
  private Vertx vertx;

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router.get("/:friendId/:offset").handler(this::getChatMessages);
    return router;
  }

  public void getChatMessages(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    HttpServerRequest request = routingContext.request();

    int userId = routingContext.user().principal().getInteger("userId");
    int friendId = Integer.parseInt(request.getParam("friendId"));
    int offset = Integer.parseInt(request.getParam("offset"));

    if (offset == 0) {
      // First load
      Future<List<WsMessage>> chatMessages = chatCacheService.getCacheMessage(userId, friendId);
      chatMessages.onSuccess(
          wsMessages -> {
            log.trace("Cache hit");
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.put("messages", wsMessages);
            jsonResponse.put("newOffset", offset + wsMessages.size());
            ControllerUtil.jsonResponse(response, jsonResponse);
          });
      chatMessages.onFailure(throwable -> getFromDB(userId, friendId, offset, response, true));
    } else {
      // Load more
      getFromDB(userId, friendId, offset, response, false);
    }
  }

  private void getFromDB(
      int userId, int friendId, int offset, HttpServerResponse response, boolean isCached) {
    chatDBService
        .getChatMessages(userId, friendId, offset)
        .onComplete(
            result -> {
              if (result.succeeded()) {
                List<WsMessage> listMessage = result.result();

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.put("messages", listMessage);
                jsonResponse.put("newOffset", offset + listMessage.size());

                ControllerUtil.jsonResponse(response, jsonResponse);

                // Cache
                if (isCached) {
                  chatCacheService.cacheListMessage(listMessage, userId, friendId);
                }
              } else {
                log.error("Error when get chat messages", result.cause());
                response.setStatusCode(500).end();
              }
            });
  }
}
