package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.service.AsyncWorkerService;
import dagger.Module;
import dagger.Provides;
import org.redisson.api.RedissonClient;

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
  public RedissonClient provideRedissonClient(RedisCache redisCache) {
    return redisCache.getRedissonClient();
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

  @Provides
  @Singleton
  FriendCacheService provideFriendCacheService(
      RedissonClient redissonClient, AsyncWorkerService workerService, CacheConfig cacheConfig) {
    return FriendCacheServiceImpl.builder()
        .asyncWorkerService(workerService)
        .cacheConfig(cacheConfig)
        .redissonClient(redissonClient)
        .build();
  }
}
