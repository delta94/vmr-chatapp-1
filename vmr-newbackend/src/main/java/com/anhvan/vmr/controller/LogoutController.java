package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.TokenCacheService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

@Builder
@AllArgsConstructor
@Log4j2
public class LogoutController extends BaseController {
  private TokenCacheService tokenCacheService;

  @Override
  public Future<BaseResponse> handlePost(BaseRequest baseRequest) {
    Promise<BaseResponse> logoutPromise = Promise.promise();

    // Get token from header
    String jwtToken = baseRequest.getRequest().getHeader("Authorization").substring(7);

    // Log
    log.debug("handlePost - logout user: jwtToken={}", jwtToken);

    // Add to blacklist
    tokenCacheService
        .addToBlackList(jwtToken)
        .onComplete(
            ar -> {
              log.debug("After logout user");

              if (ar.succeeded()) {
                // Logout success
                logoutPromise.complete(
                    BaseResponse.builder()
                        .statusCode(HttpResponseStatus.OK.code())
                        .message("Logout successfully")
                        .build());
              } else {
                // Add jwtToken to blacklist failed
                log.error("Error in handlePost - logout user: jwtToken={}", jwtToken, ar.cause());
                logoutPromise.complete(
                    BaseResponse.builder()
                        .statusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                        .message("Fail to logout")
                        .build());
              }
            });

    return logoutPromise.future();
  }
}
