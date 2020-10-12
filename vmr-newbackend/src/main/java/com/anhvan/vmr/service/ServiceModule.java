package com.anhvan.vmr.service;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDatabaseService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ServiceModule {
  @Provides
  @Singleton
  public UserService provideUserService(
      UserDatabaseService dbService, UserCacheService cacheService) {
    return new UserServiceImpl(cacheService, dbService);
  }
}
