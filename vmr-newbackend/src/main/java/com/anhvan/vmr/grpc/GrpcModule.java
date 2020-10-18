package com.anhvan.vmr.grpc;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.cache.FriendCacheService;
import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.database.WalletDatabaseService;
import com.anhvan.vmr.service.FriendService;
import com.anhvan.vmr.service.TrackerService;
import com.anhvan.vmr.service.UserService;
import com.anhvan.vmr.websocket.WebSocketService;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.grpc.BindableService;

import javax.inject.Singleton;

@Module
public class GrpcModule {
  public static final String GRPC_RESPONSE_TIME_METRIC = "grpc_response_time";

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideSampleServiceImpl() {
    return new GrpcSampleServiceImpl();
  }

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideFriendServiceImpl(
      UserDatabaseService userDatabaseService,
      FriendDatabaseService friendDatabaseService,
      WebSocketService wsService,
      FriendService friendService,
      UserService userService,
      TrackerService trackerService) {
    return GrpcFriendServiceImpl.builder()
        .userDbService(userDatabaseService)
        .friendDbService(friendDatabaseService)
        .webSocketService(wsService)
        .userService(userService)
        .friendService(friendService)
        .chatFriendListTracker(
            trackerService.getTimeTracker(GRPC_RESPONSE_TIME_METRIC, "method", "getChatFriendList"))
        .build();
  }

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideWalletServiceImpl(
      UserDatabaseService userDatabaseService,
      WalletDatabaseService walletDatabaseService,
      WebSocketService webSocketService,
      ChatCacheService chatCacheService,
      FriendCacheService friendCacheService,
      TrackerService trackerService) {
    return GrpcWalletServiceImpl.builder()
        .userDbService(userDatabaseService)
        .walletDatabaseService(walletDatabaseService)
        .webSocketService(webSocketService)
        .chatCacheService(chatCacheService)
        .friendCacheService(friendCacheService)
        .balanceTracker(
            trackerService.getTimeTracker(GRPC_RESPONSE_TIME_METRIC, "method", "getBalance"))
        .historyTracker(
            trackerService.getTimeTracker(GRPC_RESPONSE_TIME_METRIC, "method", "getHistory"))
        .transferTracker(
            trackerService.getTimeTracker(GRPC_RESPONSE_TIME_METRIC, "method", "transfer"))
        .build();
  }
}
