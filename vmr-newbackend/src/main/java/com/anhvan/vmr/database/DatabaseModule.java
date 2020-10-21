package com.anhvan.vmr.database;

import com.anhvan.vmr.consts.Metric;
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
      DatabaseService databaseService,
      AsyncWorkerService workerUtil,
      TrackerService trackerService) {
    return new UserDatabaseServiceWithTrackerImpl(
        databaseService.getPool(), workerUtil, trackerService);
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
            trackerService.getTimeTracker(Metric.DATABASE_QUERY_TIME, "method", "getHistory"))
        .transferTracker(
            trackerService.getTimeTracker(Metric.DATABASE_QUERY_TIME, "method", "transfer"))
        .build();
  }
}
