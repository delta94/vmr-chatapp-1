package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.JwtUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Builder
@Log4j2
public class RegisterController extends BaseController {
  private UserDBService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;

  @Override
  public Future<BaseResponse> handlePost(BaseRequest baseRequest) {
    Promise<BaseResponse> registerPromise = Promise.promise();

    // Get request body
    JsonObject requestBody = baseRequest.getBody();

    // Create user object
    User user = requestBody.mapTo(User.class);

    if (!validate(user)) {
      log.info(
          "Validate user info not valid uname {} - name {} - pwlen {}",
          user.getUsername(),
          user.getName(),
          user.getPassword().length());

      registerPromise.complete(
          BaseResponse.builder()
              .statusCode(HttpResponseStatus.BAD_REQUEST.code())
              .message("Username or password not valid")
              .build());
    }

    // Add user
    Future<Integer> userIdFuture = userDBService.addUser(user);
    userIdFuture.onFailure(
        throwable ->
            registerPromise.complete(
                BaseResponse.builder()
                    .statusCode(HttpResponseStatus.CONFLICT.code())
                    .message("Username existed")
                    .build()));

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

              registerPromise.complete(
                  BaseResponse.builder()
                      .statusCode(HttpResponseStatus.CREATED.code())
                      .message("Create success")
                      .data(jsonResponse)
                      .build());
            })
        .onFailure(err -> log.error("Error", err));

    // Set cache
    userIdFuture.onSuccess(
        id -> {
          user.setId(id);
          userCacheService.setUserCache(user);
          userCacheService.addUserList(user);
        });

    return registerPromise.future();
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
