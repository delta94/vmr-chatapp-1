package com.anhvan.vmr.dagger;

import com.anhvan.vmr.cache.CacheModule;
import com.anhvan.vmr.config.ConfigModule;
import com.anhvan.vmr.controller.ControllerModule;
import com.anhvan.vmr.database.DatabaseModule;
import com.anhvan.vmr.grpc.GrpcModule;
import com.anhvan.vmr.server.GrpcServer;
import com.anhvan.vmr.server.WebServerVerticle;
import com.anhvan.vmr.server.WebSocketServer;
import com.anhvan.vmr.service.ServiceModule;
import com.anhvan.vmr.websocket.WebSocketModule;
import dagger.Component;
import io.vertx.core.Vertx;

import javax.inject.Singleton;

@Component(
    modules = {
      ConfigModule.class,
      VmrModule.class,
      ControllerModule.class,
      WebSocketModule.class,
      DatabaseModule.class,
      CacheModule.class,
      GrpcModule.class,
      ServiceModule.class
    })
@Singleton
public interface VmrComponent {
  Vertx getVertx();

  WebServerVerticle getWebServer();

  WebSocketServer getWebSocketServer();

  GrpcServer getGrpcServer();

  @Component.Builder
  interface Builder {
    VmrComponent build();

    Builder configModule(ConfigModule module);
  }
}
