package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;

import java.util.List;

public interface ChatCacheService {
  void cacheMessage(Message message);

  void cacheListMessage(List<Message> messages, int user1, int user2);

  Future<List<Message>> getCacheMessage(int userId1, int userId2);
}
