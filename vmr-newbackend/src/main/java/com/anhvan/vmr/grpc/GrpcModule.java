package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.websocket.WebSocketService;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.grpc.BindableService;

import javax.inject.Singleton;

@Module
public class GrpcModule {
  @Provides
  @IntoSet
  @Singleton
  public BindableService provideSampleServiceImpl() {
    return new SampleServiceImpl();
  }

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideFriendServiceImpl(
      UserDatabaseService userDatabaseService,
      FriendDatabaseService friendDatabaseService,
      WebSocketService wsService) {
    return FriendServiceImpl.builder()
        .userDbService(userDatabaseService)
        .friendDbService(friendDatabaseService)
        .webSocketService(wsService)
        .build();
  }

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideWalletServiceImpl(UserDatabaseService userDatabaseService) {
    return WalletServiceImpl.builder().userDbService(userDatabaseService).build();
  }
}
