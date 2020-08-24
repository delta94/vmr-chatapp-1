package com.anhvan.vmr.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class MainVerticles extends AbstractVerticle {
  private final JsonObject conf;

  public MainVerticles(JsonObject conf) {
    super();
    this.conf = conf;
  }

  @Override
  public void start() {
    System.out.println("Start");
    System.out.println(conf.getString("firstName"));
    vertx.close();
  }

  @Override
  public void stop() {
    System.out.println("Stop");
  }
}
