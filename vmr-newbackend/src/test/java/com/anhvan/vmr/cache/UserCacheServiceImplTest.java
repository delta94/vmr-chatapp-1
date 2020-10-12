package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
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

    AsyncWorkerService workerUtil = Mockito.mock(AsyncWorkerService.class);
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
        .onFailure(throwable -> testContext.completeNow());
  }
}
