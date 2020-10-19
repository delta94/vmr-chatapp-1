package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
import com.anhvan.vmr.util.AsyncUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RedissonClient;

import java.util.List;

public class ChatCacheServiceWithTrackerImpl extends ChatCacheServiceImpl {
  private TimeTracker cacheMessageTracker;
  private TimeTracker cacheListMessageTracker;
  private TimeTracker getCacheMessagesTracker;

  public ChatCacheServiceWithTrackerImpl(
      RedissonClient redis,
      AsyncWorkerService workerUtil,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    super(redis, workerUtil, cacheConfig);
    cacheMessageTracker =
        trackerService.getTimeTracker("cache_query_time", "method", "cacheMessage");
    cacheListMessageTracker =
        trackerService.getTimeTracker("cache_query_time", "method", "cacheListMessage");
    getCacheMessagesTracker =
        trackerService.getTimeTracker("cache_query_time", "method", "getCacheMessages");
  }

  @Override
  public Future<Void> cacheListMessage(List<Message> messages, long user1, long user2) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = cacheListMessageTracker.start();

    super.cacheListMessage(messages, user1, user2)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<Void> cacheMessage(Message message) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = cacheMessageTracker.start();

    super.cacheMessage(message)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<List<Message>> getCacheMessages(long userId1, long userId2) {
    Promise<List<Message>> promise = Promise.promise();
    TimeTracker.Tracker tracker = getCacheMessagesTracker.start();

    super.getCacheMessages(userId1, userId2)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }
}
