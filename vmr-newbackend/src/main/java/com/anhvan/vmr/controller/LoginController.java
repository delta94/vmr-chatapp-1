package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.JwtUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import jodd.crypt.BCrypt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginController extends BaseController {
  private UserDBService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;

  protected Future<BaseResponse> handlePost(BaseRequest baseRequest) {
    // Get response promise
    Promise<BaseResponse> responsePromise = Promise.promise();

    // Get user submitted data
    User user = baseRequest.getBody().mapTo(User.class);
    log.info("Handle login for user {}", user.getUsername());

    // Response data
    JsonObject data = new JsonObject();

    // Get user from database
    Future<User> userFuture = userDBService.getUserByUsername(user.getUsername());

    Future<String> tokenFuture =
        userFuture.compose(
            dbUser -> {
              // Check password
              if (user.getPassword() != null
                  && BCrypt.checkpw(user.getPassword(), dbUser.getPassword())) {
                data.put("userId", dbUser.getId());
                userCacheService.setUserCache(dbUser);
                return jwtUtil.generate(dbUser.getId());
              } else {
                log.info("Login failed, user = {}, password not valid", user.getUsername());
                responsePromise.complete(
                    BaseResponse.builder()
                        .statusCode(HttpResponseStatus.UNAUTHORIZED.code())
                        .message("Password not valid")
                        .build());
                return Future.failedFuture("Password not valid");
              }
            },
            throwable -> {
              log.info("Login failed, user = {}, user not existed", user.getUsername(), throwable);
              responsePromise.complete(
                  BaseResponse.builder()
                      .statusCode(HttpResponseStatus.UNAUTHORIZED.code())
                      .message("Username not existed")
                      .build());
              return Future.failedFuture("Username not existed");
            });

    tokenFuture.onSuccess(
        token -> {
          data.put("jwtToken", token);

          responsePromise.complete(
              BaseResponse.builder()
                  .statusCode(HttpResponseStatus.OK.code())
                  .message("Login successfully")
                  .data(data)
                  .build());

          log.info("User {} login successfully", user.getUsername());
        });

    return responsePromise.future();
  }
}
