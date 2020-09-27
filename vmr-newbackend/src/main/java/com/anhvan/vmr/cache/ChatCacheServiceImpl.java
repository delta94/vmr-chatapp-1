package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class ChatCacheServiceImpl implements ChatCacheService {
  private RedissonClient redis;
  private AsyncWorkerUtil workerUtil;
  private CacheConfig cacheConfig;

  @Inject
  public ChatCacheServiceImpl(
      RedisCache redisCache, AsyncWorkerUtil workerUtil, CacheConfig cacheConfig) {
    this.redis = redisCache.getRedissonClient();
    this.workerUtil = workerUtil;
    this.cacheConfig = cacheConfig;
  }

  private String getKey(long id1, long id2) {
    String formatString = "vmr:chat:%d:%d";
    if (id1 < id2) {
      return String.format(formatString, id1, id2);
    } else {
      return String.format(formatString, id2, id1);
    }
  }

  @Override
  public void cacheMessage(Message message) {
    workerUtil.execute(
        () -> {
          RList<Message> chatMessages =
              redis.getList(getKey(message.getSenderId(), message.getReceiverId()));
          chatMessages.add(message);
          if (chatMessages.size() > cacheConfig.getNumMessagesCached()) {
            chatMessages.remove(0);
          }
          chatMessages.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
        });
  }

  @Override
  public void cacheListMessage(List<Message> messages, int user1, int user2) {
    workerUtil.execute(
        () -> {
          RList<Message> chatMessages = redis.getList(getKey(user1, user2));
          chatMessages.clear();
          chatMessages.addAll(messages);
          chatMessages.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
        });
  }

  @Override
  public Future<List<Message>> getCacheMessage(int userId1, int userId2) {
    Promise<List<Message>> messageCache = Promise.promise();

    workerUtil.execute(
        () -> {
          RList<Message> chatMessages = redis.getList(getKey(userId1, userId2));
          if (chatMessages.isExists()) {
            messageCache.complete(chatMessages.readAll());
          } else {
            messageCache.fail(
                "Cache miss - when get cache message for user " + userId1 + " : " + userId2);
          }
        });

    return messageCache.future();
  }
}
