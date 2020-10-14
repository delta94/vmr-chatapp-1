package com.anhvan.vmr.cache;

import com.anhvan.vmr.cache.exception.CacheMissException;
import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Builder
@Log4j2
public class FriendCacheServiceImpl implements FriendCacheService {
  private RedissonClient redissonClient;
  private AsyncWorkerService asyncWorkerService;
  private CacheConfig cacheConfig;

  public static final String FRIEND_LIST_KEY = "vmr:user:%d:friends";

  @Override
  public Future<Void> cacheFriendList(long userId, List<Friend> friendList) {
    Promise<Void> cachePromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));
            friendMap.clear();
            for (Friend friend : friendList) {
              friendMap.put(friend.getId(), friend);
            }
            friendMap.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            cachePromise.complete();
          } catch (Exception e) {
            log.error("Error occur when cache friend list, userId={}", userId, e);
            cachePromise.fail(e);
          }
        });

    return cachePromise.future();
  }

  @Override
  public Future<Void> cacheFriend(long userId, Friend friend) {
    Promise<Void> cachePromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));
            friendMap.put(friend.getId(), friend);
            friendMap.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            cachePromise.complete();
          } catch (Exception e) {
            log.error("Error occur when cache friend, userId={}, friend={}", userId, friend, e);
            cachePromise.fail(e);
          }
        });

    return cachePromise.future();
  }

  @Override
  public Future<List<Friend>> getFriendList(long userId) {
    Promise<List<Friend>> friendsPromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            String key = getKey(userId);
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));
            if (friendMap.isExists()) {
              friendsPromise.complete(new ArrayList<>(friendMap.values()));
            } else {
              friendsPromise.fail(new CacheMissException(key));
            }
          } catch (Exception e) {
            log.error(
                "An error occur in when get friend list of user from cache, userId={}", userId, e);
          }
        });

    return friendsPromise.future();
  }

  @Override
  public Future<Void> updateLastMessage(long userId, long friendId, Message message) {
    Promise<Void> cachePromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));

            if (!friendMap.isExists()) {
              cachePromise.complete();
              return;
            }

            Friend friend = friendMap.get(friendId);

            if (friend != null) {
              friend.setLastMessage(message.getMessage());
              friend.setLastMessageSenderId(message.getSenderId());
              friend.setLastMessageType(message.getType());
              friend.setLastMessageTimestamp(message.getTimestamp());

              friend.setNumUnreadMessage(
                  friend.getNumUnreadMessage() + (userId == message.getSenderId() ? 0 : 1));
              friendMap.put(friendId, friend);
            }

            friendMap.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            cachePromise.complete();
          } catch (Exception e) {
            log.error(
                "Error occur when update last message, userId={}, friendId={}, message={}",
                userId,
                friendId,
                message,
                e);
            cachePromise.fail(e);
          }
        });

    return cachePromise.future();
  }

  @Override
  public Future<Void> clearFriendCache(long userId) {
    Promise<Void> cachePromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));
            friendMap.clear();
            cachePromise.complete();
          } catch (Exception e) {
            log.error("Error occur when clear friend cache, userId={}", userId, e);
            cachePromise.fail(e);
          }
        });

    return cachePromise.future();
  }

  @Override
  public Future<Void> clearUnreadMessage(long userId, long friendId) {
    Promise<Void> cachePromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));

            if (!friendMap.isExists()) {
              cachePromise.complete();
              return;
            }

            Friend friend = friendMap.get(friendId);

            if (friend != null) {
              friend.setNumUnreadMessage(0);
              friendMap.put(friendId, friend);
            }

            friendMap.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            cachePromise.complete();
          } catch (Exception e) {
            log.error(
                "Error when clear unread message in cache, userId={}, friendId={}",
                userId,
                friendId,
                e);
            cachePromise.fail(e);
          }
        });

    return cachePromise.future();
  }

  @Override
  public Future<Void> removeFriend(long userId, long friendId) {
    Promise<Void> cachePromise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            RMap<Long, Friend> friendMap = redissonClient.getMap(getKey(userId));

            if (!friendMap.isExists()) {
              cachePromise.complete();
              return;
            }

            friendMap.remove(friendId);
            cachePromise.complete();
          } catch (Exception e) {
            log.error("Error when remove friend, userId={}, friendId={}", userId, friendId, e);
            cachePromise.fail(e);
          }
        });

    return cachePromise.future();
  }

  private String getKey(long userId) {
    return String.format(FRIEND_LIST_KEY, userId);
  }
}
