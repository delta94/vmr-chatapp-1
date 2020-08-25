package com.anhvan.vmr.verticles;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(DatabaseVerticle.class);

  private final JsonObject config;

  public DatabaseVerticle(JsonObject config) {
    this.config = config;
  }

  @Override
  public void start() throws Exception {
    LOGGER.info("START MySQL");
    super.start();
  }
}
