package com.anhvan.vmr.websocket;

import com.anhvan.vmr.service.JwtService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class WebSocketModule {
  @Provides
  @Singleton
  public WebSocketService provideWebsocketService(JwtService jwtService) {
    return new WebSocketServiceImpl(jwtService);
  }
}
