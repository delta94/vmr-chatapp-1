package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.WsMessage;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatCacheService {
  private RedissonClient redis;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public ChatCacheService(RedisCache redisCache, AsyncWorkerUtil workerUtil) {
    this.redis = redisCache.getRedissonClient();
    this.workerUtil = workerUtil;
  }

  private String getKey(int id1, int id2) {
    String formatString = "vmr:chat:%d:%d";
    if (id1 < id2) {
      return String.format(formatString, id1, id2);
    } else {
      return String.format(formatString, id2, id1);
    }
  }

  public void cacheMessage(WsMessage message) {
    workerUtil.execute(
        () -> {
          RList<WsMessage> chatMessages =
              redis.getList(getKey(message.getSenderId(), message.getReceiverId()));
          chatMessages.add(message);
          if (chatMessages.size() > 20) {
            chatMessages.remove(0);
          }
          chatMessages.expire(10, TimeUnit.MINUTES);
        });
  }

  public void cacheListMessage(List<WsMessage> messages, int user1, int user2) {
    workerUtil.execute(
        () -> {
          RList<WsMessage> chatMessages = redis.getList(getKey(user1, user2));
          chatMessages.addAll(messages);
          while (chatMessages.size() > 20) {
            chatMessages.remove(0);
          }
          chatMessages.expire(10, TimeUnit.MINUTES);
        });
  }

  public Future<List<WsMessage>> getCacheMessage(int userId1, int userId2) {
    Promise<List<WsMessage>> messageCache = Promise.promise();

    workerUtil.execute(
        () -> {
          RList<WsMessage> chatMessages = redis.getList(getKey(userId1, userId2));
          if (chatMessages.isExists()) {
            messageCache.complete(chatMessages.readAll());
          } else {
            messageCache.fail("Cache chat message miss");
          }
        });

    return messageCache.future();
  }
}
