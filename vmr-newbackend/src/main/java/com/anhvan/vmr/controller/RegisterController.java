package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.ControllerUtil;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Builder
@Log4j2
public class RegisterController implements Controller {
  private Vertx vertx;
  private UserDBService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router.post("/").handler(this::registerUser);
    return router;
  }

  private void registerUser(RoutingContext context) {
    JsonObject requestBody = context.getBodyAsJson();
    HttpServerResponse response = context.response();

    // Create user object
    User user = requestBody.mapTo(User.class);

    // Add user
    Future<Integer> userIdFuture = userDBService.addUser(user);
    userIdFuture.onFailure(
        throwable -> {
          response.setStatusCode(409).end();
          log.debug("Fail when add user", throwable);
        });

    // Generate token
    Future<String> tokenFuture = userIdFuture.compose(jwtUtil::generate);

    // Response to user
    CompositeFuture.all(userIdFuture, tokenFuture)
        .onComplete(
            result -> {
              int userId = result.result().resultAt(0);
              String token = result.result().resultAt(1);
              JsonObject jsonResponse = new JsonObject().put("token", token).put("userId", userId);
              ControllerUtil.jsonResponse(response, jsonResponse, 201);
            })
        .onFailure(err -> log.error("Error", err));

    // Set cache
    userIdFuture.onSuccess(
        id -> {
          user.setId(id);
          userCacheService.setUserCache(user);
          userCacheService.addUserList(user);
        });
  }
}
