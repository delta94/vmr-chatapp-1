package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;

@Getter
@Log4j2
public class RedisCache {
  private RedissonClient redissonClient;

  @Inject
  public RedisCache(CacheConfig cacheConfig) {
    try {
      redissonClient = Redisson.create(cacheConfig.getRedisConfig());
      flushAll()
          .onComplete(
              ar -> {
                if (ar.succeeded()) {
                  log.info("Connected to redis");
                } else {
                  log.info("Fail to clear cache");
                }
              });
    } catch (Exception e) {
      log.fatal("Error when create redisson instance", e);
    }
  }

  public Future<Void> flushAll() {
    Promise<Void> promise = Promise.promise();

    RKeys keys = redissonClient.getKeys();
    keys.flushallAsync()
        .onComplete(
            (unused, throwable) -> {
              if (throwable != null) {
                promise.fail(throwable);
              } else {
                promise.complete();
              }
            });

    return promise.future();
  }
}
