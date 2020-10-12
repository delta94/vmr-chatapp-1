package com.anhvan.vmr.dagger;

import com.anhvan.vmr.config.AuthConfig;
import com.anhvan.vmr.config.VertxConfig;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;

@Module
@AllArgsConstructor
@Log4j2
public class VmrModule {
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
        .setJWTOptions(new JWTOptions().setExpiresInSeconds(config.getExpire()));
  }

  @Provides
  @Singleton
  public JWTAuth provideJwtAuth(Vertx vertx, JWTAuthOptions options) {
    return JWTAuth.create(vertx, options);
  }

  @Provides
  @Singleton
  public Vertx provideVertx(VertxConfig config) {
    log.info("Vertx worker pool size = {}", config.getNumOfWorkerThread());
    return Vertx.vertx(
        new VertxOptions()
            .setPreferNativeTransport(true)
            .setWorkerPoolSize(config.getNumOfWorkerThread()));
  }
}
