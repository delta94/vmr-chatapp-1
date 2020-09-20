package com.anhvan.vmr.server;

import com.anhvan.vmr.cache.ChatCacheServiceImpl;
import com.anhvan.vmr.database.ChatDatabaseService;
import com.anhvan.vmr.websocket.WebSocketHandler;
import com.anhvan.vmr.websocket.WebSocketService;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class WebSocketFactory {
  private WebSocketService webSocketService;
  private ChatDatabaseService chatDatabaseService;
  private ChatCacheServiceImpl chatCacheService;

  @Inject
  public WebSocketFactory(
      WebSocketService webSocketService,
      ChatDatabaseService chatDatabaseService,
      ChatCacheServiceImpl chatCacheService) {
    this.webSocketService = webSocketService;
    this.chatDatabaseService = chatDatabaseService;
    this.chatCacheService = chatCacheService;
  }

  public void webSocketHandler(ServerWebSocket conn) {
    log.info("Get websocket connection {}", conn.path());

    webSocketService
        .authenticate(conn)
        .onComplete(userIdRs -> handleAfterAuthentication(userIdRs, conn));
  }

  private void handleAfterAuthentication(AsyncResult<Integer> userIdRs, ServerWebSocket conn) {
    log.debug("Auth status: {}", userIdRs.succeeded());
    if (userIdRs.succeeded()) {
      // Accept connection
      conn.accept();

      // Create new handler
      WebSocketHandler handler =
          new WebSocketHandler(
              conn, userIdRs.result(), webSocketService, chatDatabaseService, chatCacheService);

      // Handle
      handler.handle();
    } else {
      log.error(
          "Reject when connect to websocket {}",
          JsonObject.mapFrom(conn),
          userIdRs.cause());

      // Reject connection
      conn.reject();
    }
  }
}
