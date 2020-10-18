package com.anhvan.vmr.database;

import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.PasswordService;
import com.anhvan.vmr.service.TrackerService;
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
      ChatDatabaseService chatDatabaseService,
      TrackerService trackerService) {
    return WalletDatabaseServiceWithTrackerImpl.builder()
        .pool(dbService.getPool())
        .passwordService(passwordService)
        .chatDatabaseService(chatDatabaseService)
        .historyTracker(
            trackerService.getTimeTracker("database_query_time", "method", "getHistory"))
        .transferTracker(trackerService.getTimeTracker("database_query_time", "method", "transfer"))
        .build();
  }
}
