package com.anhvan.vmr.grpc;

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
}
