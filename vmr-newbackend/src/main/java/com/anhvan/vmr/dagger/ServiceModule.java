package com.anhvan.vmr.dagger;

import com.anhvan.vmr.config.RestfulAPIConfig;
import com.anhvan.vmr.server.RestfulAPI;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;

import javax.inject.Singleton;

@Module
@AllArgsConstructor
public class ServiceModule {
  public JsonObject config;
  public Vertx vertx;

  @Provides
  @Singleton
  public String sample() {
    return "Hello";
  }

  @Provides
  @Singleton
  public RestfulAPI restfulAPI(RestfulAPIConfig config, Vertx vertx) {
    return new RestfulAPI(vertx, config);
  }

  @Provides
  @Singleton
  public RestfulAPIConfig provideRestConfig() {
    JsonObject restConfig = config.getJsonObject("rest");
    return restConfig.mapTo(RestfulAPIConfig.class);
  }

  @Provides
  @Singleton
  public Vertx provideVertx() {
    return vertx;
  }
}
