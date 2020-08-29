package com.anhvan.vmr.util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class JwtUtil {
  private JWTAuth jwtAuth;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public JwtUtil(AsyncWorkerUtil workerUtil, JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
    this.workerUtil = workerUtil;
  }

  public String getTokenFromHeader(RoutingContext routingContext) {
    return routingContext.request().getHeader("Authorization").substring(7);
  }

  public Future<String> generate(int userId) {
    Promise<String> tokenPromise = Promise.promise();
    workerUtil.execute(
        () -> {
          tokenPromise.complete(jwtAuth.generateToken(new JsonObject().put("userId", userId)));
          log.trace("GEN TOKEN");
        });
    return tokenPromise.future();
  }

  public Future<String> generate(int userId, int timeToLive) {
    Promise<String> tokenPromise = Promise.promise();
    workerUtil.execute(
        () -> {
          tokenPromise.complete(
              jwtAuth.generateToken(
                  new JsonObject().put("userId", userId),
                  new JWTOptions().setExpiresInSeconds(timeToLive)));
          log.trace("GEN WS TOKEN");
        });
    return tokenPromise.future();
  }
}