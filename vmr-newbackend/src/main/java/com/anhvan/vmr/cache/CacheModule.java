package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class CacheModule {
  @Provides
  @Singleton
  public RedisCache provideRedisCache(CacheConfig config) {
    return new RedisCache(config);
  }

  @Provides
  @Singleton
  public UserCacheService provideUserCacheService(UserCacheServiceImpl impl) {
    return impl;
  }

  @Provides
  @Singleton
  public ChatCacheService provideChatCacheService(ChatCacheServiceImpl impl) {
    return impl;
  }

  @Provides
  @Singleton
  public TokenCacheService provideTokenCacheService(TokenCacheServiceImpl impl) {
    return impl;
  }
}
