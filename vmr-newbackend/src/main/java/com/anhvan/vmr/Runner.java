package com.anhvan.vmr;

import com.anhvan.vmr.config.ConfigLoader;
import com.anhvan.vmr.config.ConfigModule;
import com.anhvan.vmr.dagger.DaggerServiceComponent;
import com.anhvan.vmr.dagger.ServiceComponent;
import com.anhvan.vmr.dagger.ServiceModule;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

public class Runner {
  public static void main(String[] args) {
    // Set vertx log4j
    System.setProperty(
        "vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.Log4j2LogDelegateFactory");

    // Create vertx instance
    Vertx vertx =
        Vertx.vertx(new VertxOptions().setPreferNativeTransport(true).setWorkerPoolSize(20));

    // Load config
    ConfigLoader configLoader = new ConfigLoader(vertx);
    Future<JsonObject> config = configLoader.load();
    config.onComplete(jsonConf -> onLoadConfig(jsonConf.result(), vertx));
  }

  // Load dagger modules
  public static void onLoadConfig(JsonObject config, Vertx vertx) {
    // Service module
    ServiceModule serviceModule = new ServiceModule(vertx);
    ConfigModule configModule = new ConfigModule(config);

    // Service component
    ServiceComponent component =
        DaggerServiceComponent.builder()
            .serviceModule(serviceModule)
            .configModule(configModule)
            .build();

    // Start webserver and websocket server
    component.getWebServer().start();
    component.getWebSocketServer().start();
  }
}
