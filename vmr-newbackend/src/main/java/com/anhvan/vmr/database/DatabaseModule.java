package com.anhvan.vmr.database;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DatabaseModule {
  @Provides
  @Singleton
  public ChatDatabaseService provideChatDBService(ChatDatabaseServiceImpl impl) {
    return impl;
  }

  @Provides
  @Singleton
  public UserDatabaseService provideUserDBService(UserDatabaseServiceImpl impl) {
    return impl;
  }

  @Provides
  @Singleton
  public FriendDatabaseService provideFriendDatabaseService(FriendDatabaseServiceImpl impl) {
    return impl;
  }
}
