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

    if (!validate(user)) {
      response.setStatusCode(400).end();
      return;
    }

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
              CompositeFuture compResult = result.result();

              // Get userId from db future and token from jwt future
              int userId = compResult.resultAt(0);
              String token = compResult.resultAt(1);

              // Create resposne
              JsonObject jsonResponse = new JsonObject();
              jsonResponse.put("jwtToken", token);
              jsonResponse.put("userId", userId);

              // Send result
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

  public boolean validate(User user) {
    if (!user.getUsername().matches("^[a-zA-Z]\\w{7,}$")) {
      return false;
    }
    if (user.getPassword().length() < 8) {
      return false;
    }
    return user.getName().length() != 0;
  }
}
