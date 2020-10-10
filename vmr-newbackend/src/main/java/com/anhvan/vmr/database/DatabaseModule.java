package com.anhvan.vmr.database;

import com.anhvan.vmr.service.PasswordService;
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
      DatabaseService dbService,
      PasswordService passwordService,
      ChatDatabaseService chatDatabaseService) {
    return WalletDatabaseServiceImpl.builder()
        .pool(dbService.getPool())
        .passwordService(passwordService)
        .chatDatabaseService(chatDatabaseService)
        .build();
  }
}
