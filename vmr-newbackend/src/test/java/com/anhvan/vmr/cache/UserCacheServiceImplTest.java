package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
@SuppressWarnings("unchecked")
public class UserCacheServiceImplTest {
  RedissonClient redissonClient;
  UserCacheServiceImpl userCacheService;

  @BeforeEach
  void setUp() {
    redissonClient = Mockito.mock(RedissonClient.class);
    RedisCache redisCache = Mockito.mock(RedisCache.class);
    Mockito.when(redisCache.getRedissonClient()).thenReturn(redissonClient);

    AsyncWorkerUtil workerUtil = Mockito.mock(AsyncWorkerUtil.class);
    Mockito.doAnswer(
            invocationOnMock -> {
              Runnable job = invocationOnMock.getArgument(0);
              job.run();
              return null;
            })
        .when(workerUtil)
        .execute(ArgumentMatchers.any());

    CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);
    Mockito.when(cacheConfig.getTimeToLive()).thenReturn(20);
    Mockito.when(cacheConfig.getNumMessagesCached()).thenReturn(20);

    userCacheService = new UserCacheServiceImpl(redisCache, workerUtil, cacheConfig);
  }

  @Test
  void testSetUserList() {
    RQueue<User> userSet = (RQueue<User>) Mockito.mock(RQueue.class);
    List<User> userList = (List<User>) Mockito.mock(List.class);
    Mockito.when(redissonClient.<User>getQueue("vmr:users")).thenReturn(userSet);
    userCacheService.setUserList(userList);
    Mockito.verify(userSet).clear();
    Mockito.verify(userSet).addAll(userList);
    Mockito.verify(userSet).expire(20, TimeUnit.SECONDS);
  }

  @Test
  void testAddUserToList() {
    RQueue<User> userSet = (RQueue<User>) Mockito.mock(RQueue.class);
    Mockito.when(redissonClient.<User>getQueue("vmr:users")).thenReturn(userSet);
    User user = Mockito.mock(User.class);
    Mockito.when(userSet.isExists()).thenReturn(true);

    userCacheService.addUserList(user);

    Mockito.verify(userSet).add(user);
    Mockito.verify(userSet).expire(20, TimeUnit.SECONDS);
  }

  @Test
  void testAddUserToCache() {
    RMap<String, String> userInfoMap = (RMap<String, String>) Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<String, String>getMap("vmr:user:1:info")).thenReturn(userInfoMap);
    User user = User.builder().id(1).active(true).username("Anh Van").name("Anh Van").build();

    userCacheService.setUserCache(user);

    Mockito.verify(userInfoMap).put(ArgumentMatchers.eq("name"), ArgumentMatchers.anyString());
    Mockito.verify(userInfoMap).put(ArgumentMatchers.eq("username"), ArgumentMatchers.anyString());
    Mockito.verify(userInfoMap).expire(20, TimeUnit.SECONDS);
  }

  @Test
  void testGetUserFromCache(VertxTestContext testContext) {
    RMap<String, String> userInfoMap = (RMap<String, String>) Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<String, String>getMap("vmr:user:1:info")).thenReturn(userInfoMap);
    Mockito.when(userInfoMap.isExists()).thenReturn(true);
    Mockito.when(userInfoMap.get("name")).thenReturn("Anh Van");
    Mockito.when(userInfoMap.get("username")).thenReturn("anhvan");

    userCacheService
        .getUserCache(1)
        .onSuccess(
            user -> {
              Assertions.assertEquals("anhvan", user.getUsername());
              Assertions.assertEquals("Anh Van", user.getName());
              testContext.completeNow();
            })
        .onFailure(testContext::failNow);
  }

  @Test
  void testGetUserNotExistFromCache(VertxTestContext testContext) {
    RMap<String, String> userInfoMap = (RMap<String, String>) Mockito.mock(RMap.class);
    Mockito.when(redissonClient.<String, String>getMap("vmr:user:1:info")).thenReturn(userInfoMap);
    Mockito.when(userInfoMap.isExists()).thenReturn(false);

    userCacheService
        .getUserCache(1)
        .onSuccess(user -> testContext.failNow(new Exception("This call must failue")))
        .onFailure(
            throwable -> testContext.completeNow());
  }
}
