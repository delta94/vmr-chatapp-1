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
  public BindableService provideTransferServiceImpl() {
    return new TransferServiceImpl();
  }

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideUserServiceImpl(UserDatabaseService service) {
    return UserServiceImpl.builder().userDbService(service).build();
  }

  @Provides
  @IntoSet
  @Singleton
  public BindableService provideFriendServiceImpl(
      FriendDatabaseService service, WebSocketService wsService) {
    return FriendServiceImpl.builder().friendDbService(service).webSocketService(wsService).build();
  }
}
