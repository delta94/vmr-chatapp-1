package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.AuthConfig;
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
public class TokenCacheServiceImpl implements TokenCacheService {
  private RedissonClient redis;
  private AuthConfig authConfig;

  @Inject
  public TokenCacheServiceImpl(RedisCache redisCache, AuthConfig authConfig) {
    this.redis = redisCache.getRedissonClient();
    this.authConfig = authConfig;
  }

  @Override
  public void addToBlackList(String token) {
    String key = getKey(token);
    RBucket<Boolean> expireValue = redis.getBucket(key);
    expireValue.setAsync(true);
    expireValue.expireAsync(authConfig.getExpire(), TimeUnit.SECONDS);
  }

  @Override
  public Future<Boolean> checkExistInBacklist(String token) {
    Promise<Boolean> existPromise = Promise.promise();

    redis
        .getBucket(getKey(token))
        .isExistsAsync()
        .onComplete(
            (exist, throwable) -> {
              if (throwable != null) {
                log.warn("Error when get result from redis", throwable);
                existPromise.fail(throwable);
              } else {
                existPromise.complete(exist);
              }
            });

    return existPromise.future();
  }

  private String getKey(String token) {
    return String.format("vmr:jwt:%s:expire", token);
  }
}
