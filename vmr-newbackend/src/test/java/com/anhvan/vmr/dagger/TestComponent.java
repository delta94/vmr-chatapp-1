package com.anhvan.vmr.dagger;

import com.anhvan.vmr.cache.CacheModule;
import com.anhvan.vmr.config.ConfigModule;
import com.anhvan.vmr.controller.ControllerModule;
import com.anhvan.vmr.database.DatabaseModule;
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
public abstract class TestComponent {
  interface Builder {
    TestComponent build();

    Builder configModule(ConfigModule configModule);
  }
}
