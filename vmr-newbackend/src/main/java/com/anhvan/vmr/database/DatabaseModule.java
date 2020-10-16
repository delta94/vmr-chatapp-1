package com.anhvan.vmr.database;

import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.PasswordService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DatabaseModule {
  @Provides
  @Singleton
  public ChatDatabaseService provideChatDBService(DatabaseService dbService) {
    return new ChatDatabaseServiceImpl(dbService.getPool());
  }

  @Provides
  @Singleton
  public UserDatabaseService provideUserDBService(
      DatabaseService databaseService, AsyncWorkerService workerUtil) {
    return new UserDatabaseServiceImpl(databaseService.getPool(), workerUtil);
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
