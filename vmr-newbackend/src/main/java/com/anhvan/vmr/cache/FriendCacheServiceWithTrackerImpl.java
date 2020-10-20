package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
import com.anhvan.vmr.util.AsyncUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RedissonClient;

import java.util.List;

public class FriendCacheServiceWithTrackerImpl extends FriendCacheServiceImpl {
  private TimeTracker friendCacheServiceTracker;

  public FriendCacheServiceWithTrackerImpl(
      RedissonClient redissonClient,
      AsyncWorkerService asyncWorkerService,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    super(redissonClient, asyncWorkerService, cacheConfig);
    friendCacheServiceTracker =
        trackerService.getTimeTracker("cache_query_time", "method", "friend_cache_service_methods");
  }

  @Override
  public Future<Void> cacheFriendList(long userId, List<Friend> friendList) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = friendCacheServiceTracker.start();

    super.cacheFriendList(userId, friendList)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<Void> cacheFriend(long userId, Friend friend) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = friendCacheServiceTracker.start();

    super.cacheFriend(userId, friend)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<Void> removeFriend(long userId, long friendId) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = friendCacheServiceTracker.start();

    super.removeFriend(userId, friendId)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<List<Friend>> getFriendList(long userId) {
    Promise<List<Friend>> promise = Promise.promise();
    TimeTracker.Tracker tracker = friendCacheServiceTracker.start();

    super.getFriendList(userId)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<Void> updateLastMessageForBoth(long userId, long friendId, Message message) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = friendCacheServiceTracker.start();

    super.updateLastMessageForBoth(userId, friendId, message)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }
}
