package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.ControllerUtil;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jodd.crypt.BCrypt;

import javax.inject.Inject;

public class LoginController implements Controller {
  private Vertx vertx;
  private UserDBService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;

  @Inject
  public LoginController(
      Vertx vertx,
      UserDBService userDBService,
      JwtUtil jwtUtil,
      UserCacheService userCacheService) {
    this.vertx = vertx;
    this.userDBService = userDBService;
    this.jwtUtil = jwtUtil;
    this.userCacheService = userCacheService;
  }

  @Override
  public Router getRouter() {
    Router result = Router.router(vertx);
    result.post("/").handler(this::handleLogin);
    return result;
  }

  public void handleLogin(RoutingContext context) {
    JsonObject body = context.getBodyAsJson();
    HttpServerResponse response = context.response();
    User user = body.mapTo(User.class);

    Future<User> userFuture = userDBService.getUserByUsername(user.getUsername());
    userFuture
        .onSuccess(
            dbUser -> {
              if (BCrypt.checkpw(user.getPassword(), dbUser.getPassword())) {
                jwtUtil
                    .generate(dbUser.getId())
                    .onSuccess(
                        token -> {
                          JsonObject res =
                              new JsonObject().put("jwtToken", token).put("userId", dbUser.getId());
                          ControllerUtil.jsonResponse(response, res);
                        });
                userCacheService.setUserCache(dbUser);
              } else {
                response.setStatusCode(401).end();
              }
            })
        .onFailure(throwable -> response.setStatusCode(401).end());
  }
}
