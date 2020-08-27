package com.anhvan.vmr.cache;

import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.inject.Inject;

@Getter
public class RedisCache {
  private RedissonClient redissonClient;

  @Inject
  public RedisCache() {
    try {
      Config config =
          Config.fromYAML(RedisCache.class.getClassLoader().getResourceAsStream("redis.yml"));
      redissonClient = Redisson.create(config);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void dispose() {
    if (redissonClient != null) {
      redissonClient.shutdown();
    }
  }
}
