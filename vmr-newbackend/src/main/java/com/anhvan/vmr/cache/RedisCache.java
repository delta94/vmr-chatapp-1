package com.anhvan.vmr.cache;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Getter
@Log4j2
public class RedisCache {
  private RedissonClient redissonClient;

  public RedisCache() {
    try {
      Config config =
          Config.fromYAML(RedisCache.class.getClassLoader().getResourceAsStream("redis.yml"));
      redissonClient = Redisson.create(config);
      log.info("Connected to redis");
    } catch (Exception e) {
      log.error("Error when create redisson instance", e);
    }
  }
}
