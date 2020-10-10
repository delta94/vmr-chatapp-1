package com.anhvan.vmr.websocket;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDatabaseService;
import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.model.Message;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
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
  private ServerWebSocket conn;
  private long userId;
  private WebSocketService webSocketService;
  private ChatDatabaseService chatDatabaseService;
  private ChatCacheService chatCacheService;

  public void handle() {
    // Add connection to hash map
    webSocketService.addConnection(userId, conn);

    // Broadcast online status
    webSocketService.broadCast(WebSocketMessage.builder().type("ONLINE").data(userId).build());

    // Remove on close
    conn.closeHandler(unused -> handleCloseConnection());

    // On message
    conn.textMessageHandler(this::handleMessage);
  }

  private void handleCloseConnection() {
    // Remove connection from concurent hash map
    webSocketService.removeConnection(userId, conn);
    if (!webSocketService.checkOnline(userId)) {
      webSocketService.broadCast(WebSocketMessage.builder().type("OFFLINE").data(userId).build());
    }
  }

  private void handleMessage(String msg) {
    // Parse message
    JsonObject jsonMessage = new JsonObject(msg);
    String type = jsonMessage.getString("type");

    // Handle chat message
    if (type.equals("CHAT")) {
      JsonObject data = jsonMessage.getJsonObject("data");
      Message message = data.mapTo(Message.class);
      message.setSenderId(userId);
      message.setTimestamp(Instant.now().getEpochSecond());
      handleChat(message);
    }
  }

  private void handleChat(Message message) {
    chatDatabaseService
        .addChat(message)
        .onSuccess(
            id -> {
              chatCacheService.cacheMessage(message);
              webSocketService.sendTo(
                  userId, WebSocketMessage.builder().type("SEND_BACK").data(message).build());
              webSocketService.sendTo(
                  message.getReceiverId(),
                  WebSocketMessage.builder().type("CHAT").data(message).build());
            })
        .onFailure(throwable -> log.error("Error when at chat at WebSocketHandler", throwable));
  }
}
