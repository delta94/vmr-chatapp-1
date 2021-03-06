package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
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
  public UserCacheService provideUserCacheService(
      RedissonClient redissonClient,
      CacheConfig cacheConfig,
      AsyncWorkerService workerService,
      TrackerService trackerService) {
    return new UserCacheServiceWithTrackerImpl(
        redissonClient, workerService, cacheConfig, trackerService);
  }

  @Provides
  @Singleton
  public ChatCacheService provideChatCacheService(
      RedissonClient redissonClient,
      AsyncWorkerService asyncWorkerService,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    return new ChatCacheServiceWithTrackerImpl(
        redissonClient, asyncWorkerService, cacheConfig, trackerService);
  }

  @Provides
  @Singleton
  public TokenCacheService provideTokenCacheService(TokenCacheServiceImpl impl) {
    return impl;
  }

  @Provides
  @Singleton
  FriendCacheService provideFriendCacheService(
      RedissonClient redissonClient,
      AsyncWorkerService workerService,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    return new FriendCacheServiceWithTrackerImpl(
        redissonClient, workerService, cacheConfig, trackerService);
  }

  @Provides
  @Singleton
  HistoryCacheService provideHistoryCacheService(
      RedissonClient redissonClient,
      AsyncWorkerService workerService,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    return new HistoryCacheServiceImpl(redissonClient, workerService, cacheConfig, trackerService);
  }
}
