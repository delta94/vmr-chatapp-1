package com.anhvan.vmr.util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

import javax.inject.Inject;

public class JwtUtil {
  private JWTAuth jwtAuth;

  @Inject
  public JwtUtil(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  public Future<String> generate(int userId) {
    Promise<String> tokenPromise = Promise.promise();
    tokenPromise.complete(jwtAuth.generateToken(new JsonObject().put("userId", userId)));
    return tokenPromise.future();
  }
}
