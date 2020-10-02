package com.anhvan.vmr.database;

import com.anhvan.vmr.util.PasswordUtil;
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

  @Provides
  @Singleton
  public WalletDatabaseService provideWalletDatabaseService(
      DatabaseService dbService, PasswordUtil passwordUtil) {
    return WalletDatabaseServiceImpl.builder()
        .pool(dbService.getPool())
        .passwordUtil(passwordUtil)
        .build();
  }
}
