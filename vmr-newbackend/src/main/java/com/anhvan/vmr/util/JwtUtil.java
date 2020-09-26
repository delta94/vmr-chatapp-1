package com.anhvan.vmr.util;

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
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class JwtUtil {
  private JWTAuth jwtAuth;
  private AsyncWorkerUtil workerUtil;
  private Algorithm algorithm;

  @Inject
  public JwtUtil(AsyncWorkerUtil workerUtil, JWTAuth jwtAuth, AuthConfig authConfig) {
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
        () -> {
          tokenPromise.complete(jwtAuth.generateToken(new JsonObject().put("userId", userId)));
          log.debug("Generate token for user {}", userId);
        });

    return tokenPromise.future();
  }

  public Future<String> generate(long userId, int timeToLive) {
    Promise<String> tokenPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          tokenPromise.complete(
              jwtAuth.generateToken(
                  new JsonObject().put("userId", userId),
                  new JWTOptions().setExpiresInSeconds(timeToLive)));
          log.debug("Generate token for user {} with ttl {}", userId, timeToLive);
        });

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
            log.info("Parse jwt for user {} successfully", userId);
          } else {
            log.info("Parse jwt fail", userAsyncResult.cause());
            userIdPromise.fail(userAsyncResult.cause());
          }
        });

    return userIdPromise.future();
  }

  public long authenticateBlocking(@NonNull String token) {
    DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
    long value = jwt.getClaim("userId").asLong();
    log.debug("Get user id from jwt {}", value);
    return value;
  }
}
