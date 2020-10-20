package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
import com.anhvan.vmr.util.AsyncUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RedissonClient;

public class UserCacheServiceWithTrackerImpl extends UserCacheServiceImpl {
  private TimeTracker userTracker;

  public UserCacheServiceWithTrackerImpl(
      RedissonClient redissonClient,
      AsyncWorkerService workerUtil,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    super(redissonClient, workerUtil, cacheConfig);
    this.userTracker =
        trackerService.getTimeTracker("cache_query_time", "method", "user_cache_service_methods");
  }

  @Override
  public Future<User> getUserCache(long userId) {
    Promise<User> promise = Promise.promise();
    TimeTracker.Tracker tracker = userTracker.start();

    super.getUserCache(userId)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<Void> setUserCache(User user) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = userTracker.start();

    super.setUserCache(user)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }
}
