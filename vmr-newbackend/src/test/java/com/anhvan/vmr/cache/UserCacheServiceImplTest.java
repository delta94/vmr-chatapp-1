package com.anhvan.vmr.cache;

import com.anhvan.vmr.cache.exception.CacheMissException;
import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

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

    userCacheService = new UserCacheServiceImpl(redissonClient, workerUtil, cacheConfig);
  }

  @Test
  void testCacheUser(VertxTestContext testContext) {
    RBucket<User> rBucket = Mockito.mock(RBucket.class);
    Mockito.when(redissonClient.<User>getBucket("vmr:user:1:info")).thenReturn(rBucket);
    User user = User.builder().name("Dang Anh Van").username("danganhvan").id(1).build();
    userCacheService
        .cacheUser(user)
        .onComplete(
            ar -> {
              Mockito.verify(rBucket).set(user);
              Mockito.verify(rBucket).expire(20, TimeUnit.SECONDS);
              testContext.completeNow();
            });
  }

  @Test
  void testGetCachedUser(VertxTestContext vertxTestContext) {
    RBucket<User> rBucket = Mockito.mock(RBucket.class);
    Mockito.when(redissonClient.<User>getBucket("vmr:user:1:info")).thenReturn(rBucket);
    User user = User.builder().name("Dang Anh Van").username("danganhvan").id(1).build();
    Mockito.when(rBucket.get()).thenReturn(user);
    Mockito.when(rBucket.isExists()).thenReturn(true);
    userCacheService
        .getUser(1)
        .onComplete(
            ar -> {
              Assertions.assertEquals(1, ar.result().getId());
              vertxTestContext.completeNow();
            });
  }

  @Test
  void testGetCachedUserFailed(VertxTestContext vertxTestContext) {
    RBucket<User> rBucket = Mockito.mock(RBucket.class);
    Mockito.when(redissonClient.<User>getBucket("vmr:user:1:info")).thenReturn(rBucket);
    Mockito.when(rBucket.isExists()).thenReturn(false);
    userCacheService
        .getUser(1)
        .onComplete(
            ar -> {
              Assertions.assertTrue(ar.failed());
              Assertions.assertTrue(ar.cause() instanceof CacheMissException);
              vertxTestContext.completeNow();
            });
  }
}
