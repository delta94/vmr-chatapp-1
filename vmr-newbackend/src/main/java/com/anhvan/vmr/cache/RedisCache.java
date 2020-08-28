package com.anhvan.vmr.cache;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Getter
public class RedisCache {
  private static final Logger LOGGER = LogManager.getLogger(RedisCache.class);

  private RedissonClient redissonClient;

  public RedisCache() {
    try {
      Config config =
          Config.fromYAML(RedisCache.class.getClassLoader().getResourceAsStream("redis.yml"));
      redissonClient = Redisson.create(config);
      LOGGER.info("Connected to redis");
    } catch (Exception e) {
      LOGGER.error("Error when create redisson instance", e);
    }
  }
}
