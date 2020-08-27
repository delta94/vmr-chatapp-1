package com.anhvan.vmr;

import com.anhvan.vmr.config.ConfigLoader;
import com.anhvan.vmr.dagger.DaggerServiceComponent;
import com.anhvan.vmr.dagger.ServiceComponent;
import com.anhvan.vmr.dagger.ServiceModule;
import com.anhvan.vmr.server.WebServer;
import com.anhvan.vmr.server.WebSocketServer;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

public class Runner {
  public static void main(String[] args) {
    // Create vertx instance
    Vertx vertx = Vertx.vertx(new VertxOptions().setPreferNativeTransport(true));

    // Load config
    ConfigLoader configLoader = new ConfigLoader(vertx);
    Future<JsonObject> config = configLoader.load();
    config.onComplete(jsonConf -> onLoadConfig(jsonConf.result(), vertx));
  }

  // Load dagger modules
  public static void onLoadConfig(JsonObject config, Vertx vertx) {
    ServiceModule module = new ServiceModule(config, vertx);
    ServiceComponent component = DaggerServiceComponent.builder().serviceModule(module).build();
    WebServer restfulAPI = component.getRestfulAPI();
    restfulAPI.start();
    WebSocketServer ws = component.getWebSocketServer();
    ws.start();
  }
}
