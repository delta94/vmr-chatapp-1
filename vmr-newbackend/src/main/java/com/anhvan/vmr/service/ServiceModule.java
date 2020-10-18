package com.anhvan.vmr.service;

import com.anhvan.vmr.cache.FriendCacheService;
import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import dagger.Module;
import dagger.Provides;
import io.micrometer.core.instrument.MeterRegistry;

import javax.inject.Singleton;

@Module
public class ServiceModule {
  @Provides
  @Singleton
  public UserService provideUserService(
      UserDatabaseService dbService, UserCacheService cacheService) {
    return new UserServiceImpl(cacheService, dbService);
  }

  @Provides
  @Singleton
  public FriendService provideFriendService(
      FriendDatabaseService friendDatabaseService, FriendCacheService friendCacheService) {
    return new FriendServiceImpl(friendDatabaseService, friendCacheService);
  }

  @Provides
  @Singleton
  public TrackerService provideTrackerService(MeterRegistry meterRegistry) {
    return new TrackerServiceImpl(meterRegistry);
  }
}
