package com.anhvan.vmr.websocket;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.model.WsMessage;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Log4j2
public class WebSocketHandler {
  private Vertx vertx;
  private ServerWebSocket conn;
  private int userId;
  private WebSocketService webSocketService;
  private ChatDBService chatDBService;
  private ChatCacheService chatCacheService;

  public void handle() {
    // Add connection to hash map
    webSocketService.addConnection(userId, conn);

    // Remove on close
    conn.closeHandler(unused -> webSocketService.removeConnection(userId, conn));

    // On message
    conn.textMessageHandler(
        msg -> {
          WsMessage message = Json.decodeValue(msg, WsMessage.class);
          handleMessage(
              message.toBuilder()
                  .senderId(userId)
                  .timestamp(Instant.now().getEpochSecond())
                  .build());
        });
  }

  public void handleMessage(WsMessage message) {
    if (message.getType().equals("CHAT")) {
      chatDBService.addChat(message);
      chatCacheService.cacheMessage(message);
      webSocketService.sendTo(message.getReceiverId(), message);
    }
  }
}
