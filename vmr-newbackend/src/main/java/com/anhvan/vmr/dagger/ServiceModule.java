package com.anhvan.vmr.dagger;

import com.anhvan.vmr.cache.RedisCache;
import com.anhvan.vmr.config.AuthConfig;
import com.anhvan.vmr.config.ServerConfig;
import com.anhvan.vmr.server.RouterFactory;
import com.anhvan.vmr.server.WebServer;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.AllArgsConstructor;

import javax.inject.Singleton;

@Module
@AllArgsConstructor
public class ServiceModule {
  private Vertx vertx;

  @Provides
  @Singleton
  public RedisCache provideRedisCache() {
    return new RedisCache();
  }

  @Provides
  @Singleton
  public WebServer provideRestfulAPI(
      ServerConfig config, Vertx vertx, RouterFactory routerFactory) {
    return new WebServer(vertx, config, routerFactory);
  }

  @Provides
  @Singleton
  @SuppressWarnings("deprecation")
  JWTAuthOptions provideJWTAuthOptions(AuthConfig config) {
    return new JWTAuthOptions()
        .addPubSecKey(
            new PubSecKeyOptions()
                .setAlgorithm("HS256")
                .setPublicKey(config.getToken())
                .setSymmetric(true))
        .setJWTOptions(new JWTOptions().setExpiresInSeconds(3600 * 24));
  }

  @Provides
  @Singleton
  public JWTAuth provideJwtAuth(Vertx vertx, JWTAuthOptions options) {
    return JWTAuth.create(vertx, options);
  }

  @Provides
  @Singleton
  public Vertx provideVertx() {
    return vertx;
  }
}
