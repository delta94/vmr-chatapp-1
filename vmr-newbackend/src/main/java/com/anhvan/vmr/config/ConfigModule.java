package com.anhvan.vmr.config;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;

import javax.inject.Singleton;

@Module
@AllArgsConstructor
public class ConfigModule {
  private JsonObject config;

  @Provides
  @Singleton
  public DatabaseConfig provideDatabaseConfig() {
    JsonObject dbConfig = config.getJsonObject("mysql");
    return dbConfig.mapTo(DatabaseConfig.class);
  }

  @Provides
  @Singleton
  public ServerConfig provideRestConfig() {
    JsonObject restConfig = config.getJsonObject("rest");
    return restConfig.mapTo(ServerConfig.class);
  }

  @Provides
  @Singleton
  public AuthConfig provideAuthConfig() {
    JsonObject authConfig = config.getJsonObject("auth");
    return authConfig.mapTo(AuthConfig.class);
  }
}
