package com.anhvan.vmr.websocket;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.ChatDBService;
import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.model.Message;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WebSocketHandler {
  private ServerWebSocket conn;
  private int userId;
  private WebSocketService webSocketService;
  private ChatDBService chatDBService;
  private ChatCacheService chatCacheService;

  public void handle() {
    // Add connection to hash map
    webSocketService.addConnection(userId, conn);

    // Broadcast online status
    webSocketService.broadCast(WebSocketMessage.builder().type("ONLINE").data(userId).build());

    // Remove on close
    conn.closeHandler(
        unused -> {
          webSocketService.removeConnection(userId, conn);
          if (!webSocketService.checkOnline(userId)) {
            webSocketService.broadCast(
                WebSocketMessage.builder().type("OFFLINE").data(userId).build());
          }
        });

    // On message
    conn.textMessageHandler(this::handleMessage);
  }

  private void handleMessage(String msg) {
    JsonObject jsonMessage = new JsonObject(msg);
    String type = jsonMessage.getString("type");

    if (type.equals("CHAT")) {
      JsonObject data = jsonMessage.getJsonObject("data");
      Message message = data.mapTo(Message.class);
      message.setSenderId(userId);
      message.setTimestamp(Instant.now().getEpochSecond());
      handleChat(message);
    }
  }

  private void handleChat(Message message) {
    chatDBService
        .addChat(message)
        .onSuccess(
            id -> {
              chatCacheService.cacheMessage(message);
              webSocketService.sendTo(
                  userId, WebSocketMessage.builder().type("SEND_BACK").data(message).build());
              webSocketService.sendTo(
                  message.getReceiverId(),
                  WebSocketMessage.builder().type("CHAT").data(message).build());
            });
  }
}
