package com.anhvan.vmr.dagger;

import com.anhvan.vmr.cache.CacheModule;
import com.anhvan.vmr.config.ConfigModule;
import com.anhvan.vmr.controller.ControllerModule;
import com.anhvan.vmr.database.DatabaseModule;
import com.anhvan.vmr.server.WebServer;
import com.anhvan.vmr.server.WebSocketServer;
import com.anhvan.vmr.websocket.WebSocketModule;
import dagger.Component;

import javax.inject.Singleton;

@Component(
    modules = {
      ConfigModule.class,
      ServiceModule.class,
      ControllerModule.class,
      WebSocketModule.class,
      DatabaseModule.class,
      CacheModule.class
    })
@Singleton
public interface ServiceComponent {
  WebServer getWebServer();

  WebSocketServer getWebSocketServer();

  @Component.Builder
  interface Builder {
    ServiceComponent build();

    Builder serviceModule(ServiceModule module);

    Builder configModule(ConfigModule module);
  }
}
