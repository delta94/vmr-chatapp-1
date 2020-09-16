package com.anhvan.vmr.config;

import com.anhvan.vmr.cache.RedisCache;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.config.Config;

import javax.inject.Singleton;
import java.io.File;

@Module
@AllArgsConstructor
@Log4j2
public class ConfigModule {
  private JsonObject config;

  @Provides
  @Singleton
  public DatabaseConfig provideDatabaseConfig() {
    JsonObject dbConfig = config.getJsonObject("mysql");
    return dbConfig.mapTo(DatabaseConfig.class);
  }

  @Provides
  @Singleton
  public ServerConfig provideServerConfig() {
    JsonObject restConfig = config.getJsonObject("server");
    return restConfig.mapTo(ServerConfig.class);
  }

  @Provides
  @Singleton
  public AuthConfig provideAuthConfig() {
    JsonObject authConfig = config.getJsonObject("auth");
    return authConfig.mapTo(AuthConfig.class);
  }

  @Provides
  @Singleton
  public CacheConfig provideCacheConfig() {
    CacheConfig cacheConfig = new CacheConfig();

    try {
      // Redisson config
      String filePath = System.getenv("vmr-redis-config-file");

      if (filePath == null) {
        cacheConfig.setRedisConfig(
            Config.fromYAML(RedisCache.class.getClassLoader().getResourceAsStream("redis.yml")));
      } else {
        cacheConfig.setRedisConfig(Config.fromYAML(new File(filePath)));
      }

      // Application config
      JsonObject cacheObejct = config.getJsonObject("cache");
      cacheConfig.setTimeToLive(cacheObejct.getInteger("timeToLive"));
      cacheConfig.setNumMessagesCached(cacheObejct.getInteger("numMessagesCached"));
    } catch (Exception e) {
      log.error("Error when load cache config", e);
    }

    return cacheConfig;
  }

  @Provides
  @Singleton
  public VertxConfig provideVertxConfig() {
    JsonObject vertxConfig = config.getJsonObject("vertx");
    return vertxConfig.mapTo(VertxConfig.class);
  }
}
