package com.anhvan.vmr.controller;

import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.util.JwtUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SocketTokenController extends BaseController {
  private JwtUtil jwtUtil;

  @Override
  protected Future<BaseResponse> handleGet(BaseRequest baseRequest) {
    Promise<BaseResponse> responsePromise = Promise.promise();

    int userId = baseRequest.getPrincipal().getInteger("userId");
    jwtUtil
        .generate(userId, 90)
        .onSuccess(
            token -> {
              JsonObject data = new JsonObject().put("token", token);
              responsePromise.complete(
                  BaseResponse.builder()
                      .message("Get token successfully")
                      .data(data)
                      .statusCode(HttpResponseStatus.OK.code())
                      .build());
            });

    return responsePromise.future();
  }
}
