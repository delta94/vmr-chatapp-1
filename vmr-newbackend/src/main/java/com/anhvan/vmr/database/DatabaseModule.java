package com.anhvan.vmr.database;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DatabaseModule {
  @Provides
  @Singleton
  public ChatDBService provideChatDBService(ChatDBServiceImpl impl) {
    return impl;
  }

  @Provides
  @Singleton
  public UserDBService provideUserDBService(UserDBServiceImpl impl) {
    return impl;
  }
}
