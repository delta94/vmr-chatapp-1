package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.AuthConfig;
import com.anhvan.vmr.util.AsyncWorkerUtil;
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
public class TokenCacheServiceImplTest {
  private RedissonClient redissonClient;
  private TokenCacheServiceImpl tokenCacheService;

  @BeforeEach
  void setUp() {
    redissonClient = Mockito.mock(RedissonClient.class);
    RedisCache cache = Mockito.mock(RedisCache.class);
    Mockito.when(cache.getRedissonClient()).thenReturn(redissonClient);
    AuthConfig authConfig = Mockito.mock(AuthConfig.class);
    Mockito.when(authConfig.getExpire()).thenReturn(20);
    AsyncWorkerUtil asyncWorkerUtil = Mockito.mock(AsyncWorkerUtil.class);
    Mockito.doAnswer(
            invocationOnMock -> {
              Runnable job = invocationOnMock.getArgument(0);
              job.run();
              return null;
            })
        .when(asyncWorkerUtil)
        .execute(ArgumentMatchers.any());
    tokenCacheService = new TokenCacheServiceImpl(cache, authConfig, asyncWorkerUtil);
  }

  @Test
  void testAddToBlackList() {
    RBucket<Boolean> existBucket = (RBucket<Boolean>) Mockito.mock(RBucket.class);
    Mockito.when(redissonClient.<Boolean>getBucket("vmr:jwt:123:expire")).thenReturn(existBucket);
    tokenCacheService.addToBlackList("123");
    Mockito.verify(existBucket).set(true);
    Mockito.verify(existBucket).expire(20, TimeUnit.SECONDS);
  }

  @Test
  void testCheckExistInBlackList(VertxTestContext vertxTestContext) {
    RBucket<Boolean> existBucket = (RBucket<Boolean>) Mockito.mock(RBucket.class);
    Mockito.when(existBucket.isExists()).thenReturn(true);
    Mockito.when(redissonClient.<Boolean>getBucket("vmr:jwt:123:expire")).thenReturn(existBucket);
    tokenCacheService
        .checkExistInBacklist("123")
        .onSuccess(
            isExist -> {
              Assertions.assertTrue(isExist);
              vertxTestContext.completeNow();
            })
        .onFailure(vertxTestContext::failNow);
  }

  @Test
  void testCheckNotExistInBlackList(VertxTestContext vertxTestContext) {
    RBucket<Boolean> existBucket = (RBucket<Boolean>) Mockito.mock(RBucket.class);
    Mockito.when(existBucket.isExists()).thenReturn(false);
    Mockito.when(redissonClient.<Boolean>getBucket("vmr:jwt:123:expire")).thenReturn(existBucket);
    tokenCacheService
        .checkExistInBacklist("123")
        .onSuccess(
            isExist -> {
              Assertions.assertFalse(isExist);
              vertxTestContext.completeNow();
            })
        .onFailure(vertxTestContext::failNow);
  }
}
