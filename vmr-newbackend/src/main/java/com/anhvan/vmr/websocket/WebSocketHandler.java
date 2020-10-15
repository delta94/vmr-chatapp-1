package com.anhvan.vmr.websocket;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.cache.FriendCacheService;
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
  public static final String TYPE_KEY = "type";

  public static final String DATA_KEY = "data";

  private ServerWebSocket conn;
  private long userId;
  private WebSocketService webSocketService;
  private ChatDatabaseService chatDatabaseService;
  private ChatCacheService chatCacheService;
  private FriendCacheService friendCacheService;

  public void handle() {
    // Add connection to hash map
    webSocketService.addConnection(userId, conn);

    // Broadcast online status
    webSocketService.broadCast(
        WebSocketMessage.builder().type(WebSocketMessage.Type.ONLINE.name()).data(userId).build());

    // Remove on close
    conn.closeHandler(unused -> handleCloseConnection());

    // On message
    conn.textMessageHandler(this::handleMessage);
  }

  private void handleCloseConnection() {
    // Remove connection from concurent hash map
    webSocketService.removeConnection(userId, conn);
    if (!webSocketService.checkOnline(userId)) {
      webSocketService.broadCast(
          WebSocketMessage.builder()
              .type(WebSocketMessage.Type.OFFLINE.name())
              .data(userId)
              .build());
    }
  }

  private void handleMessage(String msg) {
    // Parse message
    JsonObject jsonMessage = new JsonObject(msg);
    String type = jsonMessage.getString(TYPE_KEY);

    // Handle chat message
    if (type.equals(Message.Type.CHAT.name())) {
      JsonObject data = jsonMessage.getJsonObject(DATA_KEY);
      Message message = data.mapTo(Message.class);
      message.setSenderId(userId);
      message.setTimestamp(Instant.now().getEpochSecond());
      handleChat(message);
    }
  }

  private void handleChat(Message message) {
    long receiverId = message.getReceiverId();
    chatDatabaseService
        .addChat(message)
        .onSuccess(
            id -> {
              message.setType(Message.Type.CHAT.name());
              chatCacheService.cacheMessage(message);
              friendCacheService.updateLastMessageForBoth(userId, receiverId, message);
              webSocketService.sendChatMessage(userId, receiverId, message);
            })
        .onFailure(
            throwable ->
                log.error(
                    "Error when at chat: senderId={}, receiverId={}",
                    userId,
                    receiverId,
                    throwable));
  }
}
