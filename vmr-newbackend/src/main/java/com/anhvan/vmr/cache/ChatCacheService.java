package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.WsMessage;
import io.vertx.core.Future;

import java.util.List;

public interface ChatCacheService {
  void cacheMessage(WsMessage message);

  void cacheListMessage(List<WsMessage> messages, int user1, int user2);

  Future<List<WsMessage>> getCacheMessage(int userId1, int userId2);
}
