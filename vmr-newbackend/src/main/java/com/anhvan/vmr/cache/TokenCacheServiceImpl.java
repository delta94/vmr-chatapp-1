package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.AuthConfig;
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
  public static final String TOKEN_EXPIRE_KEY = "vmr:jwt:%s:expire";

  private RedissonClient redis;
  private AuthConfig authConfig;
  private AsyncWorkerService asyncWorkerService;

  @Inject
  public TokenCacheServiceImpl(
      RedissonClient redissonClient, AuthConfig authConfig, AsyncWorkerService asyncWorkerService) {
    this.redis = redissonClient;
    this.authConfig = authConfig;
    this.asyncWorkerService = asyncWorkerService;
  }

  @Override
  public Future<Void> addToBlackList(String token) {
    log.debug("addToBlackList: token={}", token);

    Promise<Void> promise = Promise.promise();

    asyncWorkerService.execute(
        () -> {
          try {
            String key = getKey(token);
            RBucket<Boolean> expireValue = redis.getBucket(key);
            expireValue.set(true);
            expireValue.expire(authConfig.getExpire(), TimeUnit.SECONDS);
            promise.complete();
          } catch (Exception exception) {
            log.error("Error in addToBlackList: jwtToken={}", token, exception);
            promise.fail(exception);
          }
        });

    return promise.future();
  }

  @Override
  public Future<Boolean> checkExistInBlackList(String token) {
    log.debug("checkExistInBlackList: token={}", token);

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
    return String.format(TOKEN_EXPIRE_KEY, token);
  }
}
