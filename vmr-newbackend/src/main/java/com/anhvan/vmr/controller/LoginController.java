package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.consts.ResponseCode;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.JwtService;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import jodd.crypt.BCrypt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginController extends BaseController {
  private UserDatabaseService userDBService;
  private JwtService jwtService;
  private UserCacheService userCacheService;

  public Future<BaseResponse> handlePost(BaseRequest baseRequest) {
    // Get response promise
    Promise<BaseResponse> responsePromise = Promise.promise();

    // Get user submitted data
    User user = baseRequest.getBody().mapTo(User.class);
    log.info("handlePost: login user: username={}", user.getUsername());

    // Response data
    JsonObject data = new JsonObject();

    // Get user from database
    Future<User> userFuture = userDBService.getUserByUsername(user.getUsername());

    // Handle user get from database
    Function<User, Future<String>> databaseUserHandler =
        dbUser -> {
          // view user
          log.debug("Get user from database {}", dbUser);

          // Check password
          if (user.getPassword() != null
              && BCrypt.checkpw(user.getPassword(), dbUser.getPassword())) {
            data.put("userId", dbUser.getId());
            userCacheService.cacheUser(dbUser);
            return jwtService.generate(dbUser.getId());
          }

          // Password not valid
          log.info("Login failed, user = {}, password not valid", user.getUsername());
          responsePromise.complete(
              BaseResponse.builder()
                  .statusCode(HttpResponseStatus.UNAUTHORIZED.code())
                  .responseCode(ResponseCode.PASSWORD_INVALID)
                  .build());
          return Future.failedFuture("Password not valid");
        };

    // Handle get user from database fails
    Function<Throwable, Future<String>> failueHandler =
        throwable -> {
          log.info("handlePost: login failed: username={}", user.getUsername(), throwable);
          responsePromise.complete(
              BaseResponse.builder()
                  .statusCode(HttpResponseStatus.UNAUTHORIZED.code())
                  .responseCode(ResponseCode.USERNAME_NOT_EXISTED)
                  .build());
          return Future.failedFuture("Username not existed");
        };

    Future<String> tokenFuture = userFuture.compose(databaseUserHandler, failueHandler);

    tokenFuture.onSuccess(
        token -> {
          data.put("jwtToken", token);

          responsePromise.complete(
              BaseResponse.builder().statusCode(HttpResponseStatus.OK.code()).data(data).build());

          log.info("handlePost: login successfully: username={}", user.getUsername());
        });

    return responsePromise.future();
  }
}
