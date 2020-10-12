package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;

import java.util.List;

public interface ChatCacheService {
  void cacheMessage(Message message);

  void cacheListMessage(List<Message> messages, long user1, long user2);

  Future<List<Message>> getCacheMessage(long userId1, long userId2);
}
