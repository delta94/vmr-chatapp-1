package com.anhvan.vmr.websocket;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.model.Message;
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

    // Broadcast online status
    webSocketService.broadCast(Message.builder().type("ONLINE").senderId(userId).build());

    // Remove on close
    conn.closeHandler(
        unused -> {
          webSocketService.removeConnection(userId, conn);
          if (!webSocketService.checkOnline(userId)) {
            webSocketService.broadCast(
                Message.builder().type("OFFLINE").senderId(userId).build());
          }
        });

    // On message
    conn.textMessageHandler(
        msg -> {
          Message message = Json.decodeValue(msg, Message.class);
          handleMessage(
              message.toBuilder()
                  .senderId(userId)
                  .timestamp(Instant.now().getEpochSecond())
                  .build());
        });
  }

  public void handleMessage(Message message) {
    if (message.getType().equals("CHAT")) {
      chatDBService
          .addChat(message)
          .onSuccess(
              id -> {
                chatCacheService.cacheMessage(message);
                webSocketService.sendTo(userId, message.toBuilder().type("SEND_BACK").build());
                webSocketService.sendTo(message.getReceiverId(), message);
              });
    }
  }
}
