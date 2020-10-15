package com.anhvan.vmr.cache;

import com.anhvan.vmr.cache.exception.CacheMissException;
import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class FriendCacheServiceImplTest {
  RedissonClient redissonClient;
  FriendCacheServiceImpl friendCacheService;
  CacheConfig cacheConfig;

  @BeforeEach
  void setUp() {
    redissonClient = Mockito.mock(RedissonClient.class);
    RedisCache redisCache = Mockito.mock(RedisCache.class);
    Mockito.when(redisCache.getRedissonClient()).thenReturn(redissonClient);

    AsyncWorkerService workerService = Mockito.mock(AsyncWorkerService.class);
    Mockito.doAnswer(
            invocationOnMock -> {
              Runnable job = invocationOnMock.getArgument(0);
              job.run();
              return null;
            })
        .when(workerService)
        .execute(ArgumentMatchers.any());

    cacheConfig = Mockito.mock(CacheConfig.class);
    Mockito.when(cacheConfig.getTimeToLive()).thenReturn(20);
    Mockito.when(cacheConfig.getNumMessagesCached()).thenReturn(20);

    friendCacheService =
        FriendCacheServiceImpl.builder()
            .cacheConfig(cacheConfig)
            .redissonClient(redissonClient)
            .asyncWorkerService(workerService)
            .build();
  }

  @Test
  void testCacheFriendList(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Friend friend = Friend.builder().build();
    friend.setId(2);
    List<Friend> friendList = Arrays.asList(friend, friend);

    friendCacheService
        .cacheFriendList(1, friendList)
        .onComplete(
            ar -> {
              Mockito.verify(friendMap).clear();
              Mockito.verify(friendMap, Mockito.times(2))
                  .put(ArgumentMatchers.eq(2L), ArgumentMatchers.any());
              Mockito.verify(friendMap).expire(20, TimeUnit.SECONDS);
              testContext.completeNow();
            });
  }

  @Test
  void testCacheFriend(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Friend friend = Friend.builder().build();
    friend.setId(2);
    Mockito.when(friendMap.isExists()).thenReturn(true);

    friendCacheService
        .cacheFriend(1, friend)
        .onComplete(
            ar -> {
              Mockito.verify(friendMap).put(2L, friend);
              Mockito.verify(friendMap).expire(20, TimeUnit.SECONDS);
              testContext.completeNow();
            });
  }

  @Test
  void testGetFriendListCacheHit(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Friend friend = Friend.builder().build();
    friend.setId(2);
    List<Friend> friendList = Arrays.asList(friend, friend);
    Mockito.when(friendMap.isExists()).thenReturn(true);
    Mockito.when(friendMap.values()).thenReturn(friendList);

    friendCacheService
        .getFriendList(1)
        .onComplete(
            ar -> {
              Assertions.assertEquals(2, ar.result().size());
              testContext.completeNow();
            });
  }

  @Test
  void testGetFriendListCacheMiss(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Friend friend = Friend.builder().build();
    friend.setId(2);
    List<Friend> friendList = Arrays.asList(friend, friend);
    Mockito.when(friendMap.isExists()).thenReturn(false);
    Mockito.when(friendMap.values()).thenReturn(friendList);

    friendCacheService
        .getFriendList(1)
        .onComplete(
            ar -> {
              Assertions.assertTrue(ar.failed());
              Assertions.assertTrue(ar.cause() instanceof CacheMissException);
              testContext.completeNow();
            });
  }

  @Test
  void testUpdateLastMessage(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Message message =
        Message.builder()
            .senderId(1)
            .receiverId(2)
            .message("ABC")
            .type("CHAT")
            .timestamp(123)
            .build();
    Friend friend = Mockito.mock(Friend.class);
    Mockito.when(friendMap.get(2L)).thenReturn(friend);
    Mockito.when(friendMap.isExists()).thenReturn(true);

    friendCacheService
        .updateLastMessage(1, 2, message)
        .onComplete(
            ar -> {
              Mockito.verify(friend).setLastMessage(ArgumentMatchers.any());
              Mockito.verify(friend).setLastMessageTimestamp(ArgumentMatchers.anyLong());
              Mockito.verify(friend).setLastMessageSenderId(ArgumentMatchers.anyLong());
              Mockito.verify(friend).setLastMessageType(ArgumentMatchers.anyString());
              testContext.completeNow();
            });
  }

  @Test
  void testRemoveFriend(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Mockito.when(friendMap.isExists()).thenReturn(true);

    friendCacheService
        .removeFriend(1, 2)
        .onComplete(
            ar -> {
              Mockito.verify(friendMap).remove(2L);
              testContext.completeNow();
            });
  }

  @Test
  void testRemoveFriendWhenCacheMiss(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Mockito.when(friendMap.isExists()).thenReturn(false);

    friendCacheService
        .removeFriend(1, 2)
        .onComplete(
            ar -> {
              Mockito.verify(friendMap, Mockito.times(0)).remove(2L);
              testContext.completeNow();
            });
  }

  @Test
  void testClearUnreadMessages(VertxTestContext testContext) {
    RMap<Long, Friend> friendMap = Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<Long, Friend>getMap("vmr:user:1:friends")).thenReturn(friendMap);
    Mockito.when(friendMap.isExists()).thenReturn(true);
    Friend friend = Mockito.mock(Friend.class);
    Mockito.when(friendMap.get(2L)).thenReturn(friend);

    friendCacheService
        .clearUnreadMessage(1, 2)
        .onComplete(
            ar -> {
              Mockito.verify(friend, Mockito.times(1)).setNumUnreadMessage(0);
              testContext.completeNow();
            });
  }
}
