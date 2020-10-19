package com.anhvan.vmr;

import com.anhvan.vmr.configs.ConfigLoader;
import com.anhvan.vmr.configs.ConfigModule;
import com.anhvan.vmr.dagger.DaggerVmrComponent;
import com.anhvan.vmr.dagger.VmrComponent;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jodd.crypt.BCrypt;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
    VmrComponent component = DaggerVmrComponent.builder().configModule(configModule).build();

    // Start webserver and websocket server
    Vertx vertx = component.getVertx();

    // Global exception handler
    vertx.exceptionHandler(
        throwable -> log.error("Uncached exception occured in the app", throwable));

    // Deploy verticles
    vertx.deployVerticle(component.getWebServer());
    vertx.deployVerticle(component.getWebSocketServer());
    component.getGrpcServer().start();

    System.out.println(BCrypt.hashpw("12345678", BCrypt.gensalt(5)));
  }
}
