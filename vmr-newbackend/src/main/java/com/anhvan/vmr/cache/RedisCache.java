package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
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
      log.info("Connected to redis");
    } catch (Exception e) {
      log.fatal("Error when create redisson instance", e);
    }
  }

  public void shutdown() {
    redissonClient.shutdown();
  }
}
