package com.anhvan.vmr.cache;

import com.anhvan.vmr.cache.exception.CacheMissException;
import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
@Log4j2
public class UserCacheServiceImpl implements UserCacheService {
  public static final String USER_INFO_KEY = "vmr:user:%d:info";

  private RedissonClient redis;
  private AsyncWorkerService workerUtil;
  private CacheConfig cacheConfig;

  @Inject
  public UserCacheServiceImpl(
      RedissonClient redissonClient, AsyncWorkerService workerUtil, CacheConfig cacheConfig) {
    redis = redissonClient;
    this.workerUtil = workerUtil;
    this.cacheConfig = cacheConfig;
  }

  private String getUserKey(long userId) {
    return String.format(USER_INFO_KEY, userId);
  }

  @Override
  public Future<Void> setUserCache(User user) {
    log.debug("setUserCache: user={}", user);

    Promise<Void> cacheUserPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          try {
            String key = getUserKey(user.getId());
            RBucket<User> userBucket = redis.getBucket(key);
            userBucket.set(user);
            userBucket.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            cacheUserPromise.complete();
          } catch (Exception e) {
            log.error("Error in setCacheUser: user={}", user, e);
            cacheUserPromise.fail(e);
          }
        });

    return cacheUserPromise.future();
  }

  @Override
  public Future<User> getUserCache(long userId) {
    log.debug("getUserCache: userId={}", userId);

    Promise<User> userPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          try {
            RBucket<User> userInfo = redis.getBucket(getUserKey(userId));
            if (!userInfo.isExists()) {
              log.debug("getUserCache: userId={} -> cache miss", userId);
              userPromise.fail(new CacheMissException(getUserKey(userId)));
              return;
            }
            userInfo.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            userPromise.complete(userInfo.get());
          } catch (Exception e) {
            log.error("Error in getUserCache: userId={}", userId, e);
            userPromise.fail(e);
          }
        });

    return userPromise.future();
  }
}
