package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;

import java.util.List;

public interface ChatCacheService {
  Future<Void> cacheMessage(Message message);

  Future<Void> cacheListMessage(List<Message> messages, long user1, long user2);

  Future<List<Message>> getCacheMessage(long userId1, long userId2);
}
