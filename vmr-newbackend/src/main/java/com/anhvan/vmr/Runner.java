package com.anhvan.vmr;

import com.anhvan.vmr.config.ConfigLoader;
import com.anhvan.vmr.config.ConfigModule;
import com.anhvan.vmr.dagger.DaggerServiceComponent;
import com.anhvan.vmr.dagger.ServiceComponent;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Runner {
  public static void main(String[] args) {
    // Set vertx log4j
    System.setProperty(
        "vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.Log4j2LogDelegateFactory");

    // Load config
    JsonObject config = ConfigLoader.loadConfig();

    // Create config module
    ConfigModule configModule = new ConfigModule(config);

    // Service component
    ServiceComponent component =
        DaggerServiceComponent.builder().configModule(configModule).build();

    // Start webserver and websocket server
    Vertx vertx = component.getVertx();

    // Deploy verticles
    vertx.deployVerticle(component.getWebServer());
    vertx.deployVerticle(component.getWebSocketServer());
    component.getGrpcServer().start();
  }
}
