package com.anhvan.vmr.websocket;

import com.anhvan.vmr.util.JwtUtil;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class WebSocketModule {
  @Provides
  @Singleton
  public WebSocketService provideWebsocketService(JwtUtil jwtUtil) {
    return new WebSocketServiceImpl(jwtUtil);
  }
}
