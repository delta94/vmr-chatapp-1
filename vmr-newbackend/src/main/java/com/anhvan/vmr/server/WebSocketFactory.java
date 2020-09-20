package com.anhvan.vmr.server;

import com.anhvan.vmr.cache.ChatCacheServiceImpl;
import com.anhvan.vmr.database.ChatDatabaseService;
import com.anhvan.vmr.websocket.WebSocketHandler;
import com.anhvan.vmr.websocket.WebSocketServiceImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class WebSocketFactory {
  private WebSocketServiceImpl webSocketService;
  private ChatDatabaseService chatDatabaseService;
  private ChatCacheServiceImpl chatCacheService;

  @Inject
  public WebSocketFactory(
      WebSocketServiceImpl webSocketService,
      ChatDatabaseService chatDatabaseService,
      ChatCacheServiceImpl chatCacheService) {
    this.webSocketService = webSocketService;
    this.chatDatabaseService = chatDatabaseService;
    this.chatCacheService = chatCacheService;
  }

  public void handleWebSocketConn(ServerWebSocket conn) {
    log.info("Get websocket connection {}", conn.path());

    webSocketService
        .authenticate(conn)
        .onComplete(userIdRs -> handleAfterAuthentication(userIdRs, conn));
  }

  private void handleAfterAuthentication(AsyncResult<Integer> userIdRs, ServerWebSocket conn) {
    log.debug("Auth status: {}", userIdRs.succeeded());
    if (userIdRs.succeeded()) {
      // Authentication successfully
      conn.accept();

      // Create new handler
      WebSocketHandler handler =
          WebSocketHandler.builder()
              .conn(conn)
              .userId(userIdRs.result())
              .webSocketService(webSocketService)
              .chatCacheService(chatCacheService)
              .chatDatabaseService(chatDatabaseService)
              .build();

      // Handle
      handler.handle();
    } else {
      // Authentication failed
      log.error("Reject when connect to websocket {}", JsonObject.mapFrom(conn), userIdRs.cause());

      // Reject connection
      conn.reject();
    }
  }
}
