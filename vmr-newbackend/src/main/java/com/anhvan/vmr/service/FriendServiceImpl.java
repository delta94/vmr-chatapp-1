package com.anhvan.vmr.service;

import com.anhvan.vmr.cache.FriendCacheService;
import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.entity.Friend;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class FriendServiceImpl implements FriendService {
  private FriendDatabaseService friendDbService;
  private FriendCacheService friendCacheService;

  @Override
  public Future<List<Friend>> getChatFriendList(long userId) {
    Promise<List<Friend>> promise = Promise.promise();

    Future<List<Friend>> cachedFriendList = friendCacheService.getFriendList(userId);
    cachedFriendList.onComplete(
        cacheAr -> {
          if (cacheAr.succeeded()) {
            log.debug("Get friend list, userId={}, cache hit", userId);
            promise.complete(cacheAr.result());
            return;
          }

          friendDbService
              .getChatFriendList(userId)
              .onComplete(
                  dbAr -> {
                    if (dbAr.succeeded()) {
                      List<Friend> result = dbAr.result();
                      promise.complete(result);
                      friendCacheService.cacheFriendList(userId, result);
                    } else {
                      promise.fail(dbAr.cause());
                    }
                  });
        });

    return promise.future();
  }

  @Override
  public Future<Void> clearUnreadMessage(long userId, long friendId) {
    Promise<Void> promise = Promise.promise();

    friendDbService
        .clearUnreadMessage(userId, friendId)
        .compose(ignored -> friendCacheService.clearUnreadMessage(userId, friendId))
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                promise.complete();
              } else {
                Throwable cause = ar.cause();
                log.error(
                    "Error when clear unread message: userId={}, friendId={}",
                    userId,
                    friendId,
                    cause);
                promise.fail(cause);
              }
            });

    return promise.future();
  }

  @Override
  public Future<Void> acceptFriend(long userId, long friendId) {
    return friendDbService
        .acceptFriend(friendId, userId)
        .compose(ar -> friendCacheService.clearFriendCache(userId))
        .compose(ar -> friendCacheService.clearFriendCache(friendId));
  }

  @Override
  public Future<Void> removeFriend(long userId, long friendId) {
    return friendDbService
        .removeFriend(userId, friendId)
        .compose(ar -> friendCacheService.removeFriend(userId, friendId))
        .compose(ar -> friendCacheService.removeFriend(friendId, userId));
  }
}
