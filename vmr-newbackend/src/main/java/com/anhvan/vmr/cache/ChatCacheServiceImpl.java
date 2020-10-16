package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
@Log4j2
public class ChatCacheServiceImpl implements ChatCacheService {
  private static final String MESSAGES_LIST_KEY = "vmr:chat:%d:%d";

  private RedissonClient redis;
  private AsyncWorkerService workerUtil;
  private CacheConfig cacheConfig;

  @Inject
  public ChatCacheServiceImpl(
      RedisCache redisCache, AsyncWorkerService workerUtil, CacheConfig cacheConfig) {
    this.redis = redisCache.getRedissonClient();
    this.workerUtil = workerUtil;
    this.cacheConfig = cacheConfig;
  }

  private String getKey(long id1, long id2) {
    if (id1 < id2) {
      return String.format(MESSAGES_LIST_KEY, id1, id2);
    } else {
      return String.format(MESSAGES_LIST_KEY, id2, id1);
    }
  }

  @Override
  public Future<Void> cacheMessage(Message message) {
    log.debug("Start cacheMessage: message={}", message);

    Promise<Void> promise = Promise.promise();

    workerUtil.execute(
        () -> {
          try {
            RList<Message> chatMessages =
                redis.getList(getKey(message.getSenderId(), message.getReceiverId()));
            chatMessages.add(message);
            if (chatMessages.size() > cacheConfig.getNumMessagesCached()) {
              chatMessages.remove(0);
            }
            chatMessages.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            promise.complete();
          } catch (Exception e) {
            log.error("Error in cacheMessage: message={}", message, e);
            promise.fail(e);
          }
        });

    return promise.future();
  }

  @Override
  public Future<Void> cacheListMessage(List<Message> messages, long user1, long user2) {
    log.debug("Start cacheListMessage: user1={}, user2={}, messages={}", user1, user2, messages);

    Promise<Void> promise = Promise.promise();

    workerUtil.execute(
        () -> {
          try {
            RList<Message> chatMessages = redis.getList(getKey(user1, user2));
            chatMessages.clear();
            chatMessages.addAll(messages);
            chatMessages.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            promise.complete();
          } catch (Exception exception) {
            log.error(
                "Error when chache list message: user1={}, user2={}", user1, user2, exception);
            promise.fail(exception);
          }
        });

    return promise.future();
  }

  @Override
  public Future<List<Message>> getCacheMessage(long userId1, long userId2) {
    log.debug("Start getCacheMessage: user1={}, user2={}", userId1, userId2);

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
