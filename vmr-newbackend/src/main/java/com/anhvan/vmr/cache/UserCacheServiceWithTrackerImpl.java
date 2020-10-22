package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.consts.Metric;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
import com.anhvan.vmr.util.AsyncUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RedissonClient;

public class UserCacheServiceWithTrackerImpl extends UserCacheServiceImpl {
  private TimeTracker getUserTracker;
  private TimeTracker cacheUserTracker;

  public UserCacheServiceWithTrackerImpl(
      RedissonClient redissonClient,
      AsyncWorkerService workerUtil,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    super(redissonClient, workerUtil, cacheConfig);
    this.getUserTracker =
        trackerService.getTimeTracker(Metric.CACHE_QUERY_TIME, "method", "getUser");
    this.cacheUserTracker =
        trackerService.getTimeTracker(Metric.CACHE_QUERY_TIME, "method", "cacheUser");
  }

  @Override
  public Future<User> getUser(long userId) {
    Promise<User> promise = Promise.promise();
    TimeTracker.Tracker tracker = getUserTracker.start();

    super.getUser(userId)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<Void> cacheUser(User user) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = cacheUserTracker.start();

    super.cacheUser(user)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }
}
