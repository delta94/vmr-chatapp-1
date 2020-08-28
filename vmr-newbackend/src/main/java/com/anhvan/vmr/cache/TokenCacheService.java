package com.anhvan.vmr.cache;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenCacheService {
  private final Logger LOGGER = LogManager.getLogger(TokenCacheService.class);

  private RedissonClient redis;

  @Inject
  public TokenCacheService(RedisCache redisCache) {
    this.redis = redisCache.getRedissonClient();
  }

  private String getKey(String token) {
    return String.format("vmr:jwt:%s:expire", token);
  }

  public void addToBlackList(String token) {
    String key = getKey(token);
    RBucket<Boolean> expireValue = redis.getBucket(key);
    expireValue.setAsync(true);
  }

  public Future<Boolean> checkExistInBacklist(String token) {
    Promise<Boolean> existPromise = Promise.promise();

    redis
        .getBucket(getKey(token))
        .isExistsAsync()
        .onComplete(
            (exist, throwable) -> {
              if (throwable != null) {
                LOGGER.warn("Error when get result from redis", throwable);
                existPromise.fail(throwable);
              } else {
                existPromise.complete(exist);
              }
            });

    return existPromise.future();
  }
}
