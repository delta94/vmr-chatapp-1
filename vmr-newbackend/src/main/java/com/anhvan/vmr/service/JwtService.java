package com.anhvan.vmr.service;

import com.anhvan.vmr.config.AuthConfig;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JwtService {
  private JWTAuth jwtAuth;
  private AsyncWorkerService workerUtil;
  private Algorithm algorithm;

  @Inject
  public JwtService(AsyncWorkerService workerUtil, JWTAuth jwtAuth, AuthConfig authConfig) {
    this.jwtAuth = jwtAuth;
    this.workerUtil = workerUtil;
    algorithm = Algorithm.HMAC256(authConfig.getToken());
  }

  public String getTokenFromHeader(@NonNull RoutingContext routingContext) {
    return routingContext.request().getHeader("Authorization").substring(7);
  }

  public Future<String> generate(long userId) {
    Promise<String> tokenPromise = Promise.promise();

    workerUtil.execute(
        () -> tokenPromise.complete(jwtAuth.generateToken(new JsonObject().put("userId", userId))));

    return tokenPromise.future();
  }

  public Future<String> generate(long userId, int timeToLive) {
    Promise<String> tokenPromise = Promise.promise();

    workerUtil.execute(
        () ->
            tokenPromise.complete(
                jwtAuth.generateToken(
                    new JsonObject().put("userId", userId),
                    new JWTOptions().setExpiresInSeconds(timeToLive))));

    return tokenPromise.future();
  }

  public Future<Long> authenticate(@NonNull String token) {
    Promise<Long> userIdPromise = Promise.promise();

    jwtAuth.authenticate(
        new JsonObject().put("jwt", token),
        userAsyncResult -> {
          if (userAsyncResult.succeeded()) {
            long userId = userAsyncResult.result().principal().getLong("userId");
            userIdPromise.complete(userId);
          } else {
            userIdPromise.fail(userAsyncResult.cause());
          }
        });

    return userIdPromise.future();
  }

  public long authenticateBlocking(@NonNull String token) {
    DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
    return jwt.getClaim("userId").asLong();
  }
}
