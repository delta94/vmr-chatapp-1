package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.WsMessage;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
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
          if (chatMessages.size() > 50) {
            chatMessages.remove(0);
          }
          chatMessages.expire(10, TimeUnit.MINUTES);
        });
  }
}
