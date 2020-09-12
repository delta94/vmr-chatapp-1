package com.anhvan.vmr.server;

import com.anhvan.vmr.cache.ChatCacheServiceImpl;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.websocket.WebSocketHandler;
import com.anhvan.vmr.websocket.WebSocketService;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class WebSocketFactory {
  private Vertx vertx;
  private WebSocketService webSocketService;
  private ChatDBService chatDBService;
  private ChatCacheServiceImpl chatCacheService;

  @Inject
  public WebSocketFactory(
      Vertx vertx,
      WebSocketService webSocketService,
      ChatDBService chatDBService,
      ChatCacheServiceImpl chatCacheService) {
    this.vertx = vertx;
    this.webSocketService = webSocketService;
    this.chatDBService = chatDBService;
    this.chatCacheService = chatCacheService;
  }

  public void webSocketHandler(ServerWebSocket conn) {
    log.info("Get websocket connection {}", conn.path());

    webSocketService
        .authenticate(conn)
        .onComplete(
            userIdRs -> {
              log.debug("Auth status: {}", userIdRs.succeeded());
              if (userIdRs.succeeded()) {
                hanleAuthenticated(conn, userIdRs.result());
              } else {
                log.error(
                    "Reject to connecto user to websocket {}",
                    JsonObject.mapFrom(conn.path()),
                    userIdRs.cause());
                conn.reject();
              }
            });
  }

  private void hanleAuthenticated(ServerWebSocket conn, int userId) {
    conn.accept();

    // Create new handler
    WebSocketHandler handler =
        new WebSocketHandler(
            vertx, conn, userId, webSocketService, chatDBService, chatCacheService);

    // Handle
    handler.handle();
  }
}
