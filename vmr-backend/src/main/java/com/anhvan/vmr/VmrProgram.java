package com.anhvan.vmr;

import com.anhvan.vmr.utils.ConfigLoader;
import com.anhvan.vmr.verticles.ServerVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VmrProgram {
  private static final Logger LOGGER = LogManager.getLogger(VmrProgram.class);

  public static void main(String[] args) {
    LOGGER.info("Start apllication");

    // Create vertx instance
    Vertx vertx = Vertx.vertx();

    // Load config
    ConfigRetriever configRetriever = ConfigLoader.load(vertx);

    // Pass config to verticles
    configRetriever.getConfig(
        jsonResult -> {
          if (jsonResult.succeeded()) {
            LOGGER.info("Load configuration successfully");
            registerVerticle(vertx, jsonResult.result());
          } else {
            LOGGER.error("Error when load configuration", jsonResult.cause());
          }
        });
  }

  public static void registerVerticle(Vertx vertx, JsonObject configuration) {
    vertx.deployVerticle(new ServerVerticle(configuration.getJsonObject("webserver")));
  }
}
