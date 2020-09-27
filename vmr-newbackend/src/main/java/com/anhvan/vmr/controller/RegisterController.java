package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.JwtUtil;
import com.anhvan.vmr.websocket.WebSocketService;
import io.netty.handler.codec.http.HttpResponseStatus;
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
  private UserDatabaseService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;
  private WebSocketService webSocketService;

  @Override
  public Future<BaseResponse> handlePost(BaseRequest baseRequest) {
    Promise<BaseResponse> registerPromise = Promise.promise();

    // Get request body
    JsonObject requestBody = baseRequest.getBody();

    // Create user object
    User user = requestBody.mapTo(User.class);

    if (!isValid(user)) {
      log.info(
          "Validate user info not valid uname {} - name {} - pwlen {}",
          user.getUsername(),
          user.getName(),
          user.getPassword().length());

      // Return error message
      registerPromise.complete(
          BaseResponse.builder()
              .statusCode(HttpResponseStatus.BAD_REQUEST.code())
              .message("Username or password not valid")
              .build());

      return registerPromise.future();
    }

    // Create response object
    JsonObject jsonResponse = new JsonObject();

    // Add user
    Future<Long> userIdFuture = userDBService.addUser(user);

    // Failed when add user
    userIdFuture.onFailure(
        throwable ->
            registerPromise.complete(
                BaseResponse.builder()
                    .statusCode(HttpResponseStatus.CONFLICT.code())
                    .message("Username existed")
                    .build()));

    // Generate token
    Future<String> tokenFuture =
        userIdFuture.compose(
            userId -> {
              jsonResponse.put("userId", userId);
              user.setId(userId);
              userCacheService.setUserCache(user);
              userCacheService.addUserList(user);
              webSocketService.broadCast(
                  WebSocketMessage.builder().type("NEW_USER").data(user).build());
              return jwtUtil.generate(userId);
            });

    // When generate token successful
    tokenFuture.onSuccess(
        token -> {
          jsonResponse.put("jwtToken", token);
          registerPromise.complete(
              BaseResponse.builder()
                  .statusCode(HttpResponseStatus.CREATED.code())
                  .message("Create success")
                  .data(jsonResponse)
                  .build());
        });

    return registerPromise.future();
  }

  public boolean isValid(User user) {
    if (!user.getUsername().matches("^[a-zA-Z]\\w{7,}$")) {
      return false;
    }
    if (user.getPassword().length() < 8) {
      return false;
    }
    return user.getName().length() != 0;
  }
}
