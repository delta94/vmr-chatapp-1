package com.anhvan.vmr;

import com.anhvan.vmr.utils.ConfigLoader;
import com.anhvan.vmr.verticles.MainVerticles;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VmrProgram {
  private static final Logger LOGGER = LogManager.getLogger(VmrProgram.class);

  public static void main(String[] args) {
    LOGGER.info("Start apllication");
    Vertx vertx = Vertx.vertx();
    ConfigRetriever configRetriever = ConfigLoader.load(vertx);
    configRetriever.getConfig(
        jsonResult -> {
          if (jsonResult.succeeded()) {
            LOGGER.trace("Load configuration successfully");
            registerVerticle(vertx, jsonResult.result());
          } else {
            LOGGER.trace("Error when load configuration");
          }
        });
  }

  public static void registerVerticle(Vertx vertx, JsonObject configuration) {
    vertx.deployVerticle(new MainVerticles(configuration));
  }
}
