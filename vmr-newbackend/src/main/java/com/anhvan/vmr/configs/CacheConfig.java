package com.anhvan.vmr.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.redisson.config.Config;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CacheConfig {
  private Config redisConfig;
  private int timeToLive;
  private int numMessagesCached;
}
