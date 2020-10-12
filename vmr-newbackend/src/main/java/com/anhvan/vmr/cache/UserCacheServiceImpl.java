package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class UserCacheServiceImpl implements UserCacheService {
  public static final String USER_INFO_KEY = "vmr:user:%d:info";
  private RedissonClient redis;
  private AsyncWorkerService workerUtil;
  private CacheConfig cacheConfig;

  @Inject
  public UserCacheServiceImpl(
      RedisCache redisCache, AsyncWorkerService workerUtil, CacheConfig cacheConfig) {
    redis = redisCache.getRedissonClient();
    this.workerUtil = workerUtil;
    this.cacheConfig = cacheConfig;
  }

  private String getUserKey(long userId) {
    return String.format(USER_INFO_KEY, userId);
  }

  @Override
  public void setUserCache(User user) {
    String key = getUserKey(user.getId());
    RBucket<User> userBucket = redis.getBucket(key);
    workerUtil.execute(
        () -> {
          userBucket.set(user);
          userBucket.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
        });
  }

  @Override
  public Future<User> getUserCache(long userId) {
    Promise<User> userPromise = Promise.promise();

    RBucket<User> userInfo = redis.getBucket(getUserKey(userId));
    workerUtil.execute(
        () -> {
          if (!userInfo.isExists()) {
            userPromise.fail("User not exist in cache");
            return;
          }
          userInfo.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
          userPromise.complete(userInfo.get());
        });

    return userPromise.future();
  }
}
