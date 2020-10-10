package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.AuthConfig;
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
public class TokenCacheServiceImpl implements TokenCacheService {
  private RedissonClient redis;
  private AuthConfig authConfig;
  private AsyncWorkerService asyncWorkerService;

  @Inject
  public TokenCacheServiceImpl(
      RedisCache redisCache, AuthConfig authConfig, AsyncWorkerService asyncWorkerService) {
    this.redis = redisCache.getRedissonClient();
    this.authConfig = authConfig;
    this.asyncWorkerService = asyncWorkerService;
  }

  @Override
  public void addToBlackList(String token) {
    String key = getKey(token);
    RBucket<Boolean> expireValue = redis.getBucket(key);
    asyncWorkerService.execute(
        () -> {
          expireValue.set(true);
          expireValue.expire(authConfig.getExpire(), TimeUnit.SECONDS);
        });
  }

  @Override
  public Future<Boolean> checkExistInBacklist(String token) {
    Promise<Boolean> existPromise = Promise.promise();
    asyncWorkerService.execute(
        () -> {
          try {
            RBucket<Boolean> tokenBucket = redis.getBucket(getKey(token));
            existPromise.complete(tokenBucket.isExists());
          } catch (Exception e) {
            existPromise.fail(e);
            log.error("Error when get bucket from redis", e);
          }
        });
    return existPromise.future();
  }

  private String getKey(String token) {
    return String.format("vmr:jwt:%s:expire", token);
  }
}
