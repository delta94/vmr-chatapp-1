package com.anhvan.vmr.controller;

import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class IndexController extends BaseController {
  @Override
  public Future<BaseResponse> handleGet(BaseRequest baseRequest) {
    Promise<BaseResponse> responsePromise = Promise.promise();

    responsePromise.complete(BaseResponse.builder().statusCode(200).message("Hello world").build());

    return responsePromise.future();
  }
}
