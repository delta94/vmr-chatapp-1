package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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
}
