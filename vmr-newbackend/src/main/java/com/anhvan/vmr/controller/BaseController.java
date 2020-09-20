package com.anhvan.vmr.controller;

import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
public abstract class BaseController implements Controller {
  @Override
  public Router getRouter(Vertx vertx) {
    // Create router object
    Router router = Router.router(vertx);

    // Map handlers
    router.get(getPath(HttpMethod.GET)).handler(rc -> handle(rc, this::handleGet));
    router.post(getPath(HttpMethod.POST)).handler(rc -> handle(rc, this::handlePost));
    router.put(getPath(HttpMethod.PUT)).handler(rc -> handle(rc, this::handlePut));
    router.patch(getPath(HttpMethod.PATCH)).handler(rc -> handle(rc, this::handlePatch));
    router.delete(getPath(HttpMethod.DELETE)).handler(rc -> handle(rc, this::handleDelete));

    // Return
    return router;
  }

  protected void handle(
      RoutingContext routingContext, Function<BaseRequest, Future<BaseResponse>> handleFunc) {
    // Get info from routing context
    HttpServerRequest request = routingContext.request();
    HttpServerResponse response = routingContext.response();
    JsonObject body = routingContext.getBodyAsJson();

    // Log the request
    log.info("Handle request to {}", request.path());

    // User credentials
    User user = routingContext.user();
    JsonObject principal = null;
    if (user != null) {
      principal = user.principal();
    }

    // Create base request object
    BaseRequest baseRequest =
        BaseRequest.builder()
            .params(request.params())
            .queries(routingContext.queryParams())
            .request(request)
            .body(body)
            .principal(principal)
            .routingContext(routingContext)
            .build();

    // Handle
    Future<BaseResponse> requestHandler = null;
    try {
      requestHandler = handleFunc.apply(baseRequest);
    } catch (RuntimeException e) {
      log.error("Uncached exception when hanle request {}", request.path(), e);
    }

    // If request handler is not implemented
    if (requestHandler == null) {
      requestHandler = handleNotSupported();
    }

    // Handle request
    requestHandler.onComplete(
        handlerResponse -> {
          if (handlerResponse.succeeded()) {
            // Handle success
            BaseResponse handlerResponseResult = handlerResponse.result();
            response
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(handlerResponseResult.getStatusCode())
                .end(Json.encodeToBuffer(handlerResponseResult));

            // Log the response
            log.info(
                "Response to request {} with status {}",
                request.path(),
                handlerResponseResult.getStatusCode());
          } else {
            // Error
            log.error("Error when handle request {}", request, handlerResponse.cause());
            response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
          }
        });
  }

  protected Future<BaseResponse> handleGet(BaseRequest baseRequest) throws RuntimeException {
    return null;
  }

  protected Future<BaseResponse> handlePost(BaseRequest baseRequest) throws RuntimeException {
    return null;
  }

  protected Future<BaseResponse> handlePut(BaseRequest baseRequest) throws RuntimeException {
    return null;
  }

  protected Future<BaseResponse> handleDelete(BaseRequest baseRequest) throws RuntimeException {
    return null;
  }

  protected Future<BaseResponse> handlePatch(BaseRequest baseRequest) throws RuntimeException {
    return null;
  }

  private Future<BaseResponse> handleNotSupported() {
    Promise<BaseResponse> responsePromise = Promise.promise();

    BaseResponse response =
        BaseResponse.builder()
            .statusCode(HttpResponseStatus.METHOD_NOT_ALLOWED.code())
            .message("Method not allowed")
            .build();

    responsePromise.complete(response);

    return responsePromise.future();
  }

  private String getPath(HttpMethod type) {
    String method = type.toString();
    String methodName = "handle" + method.charAt(0) + method.substring(1).toLowerCase();

    try {
      RoutePath ann =
          this.getClass().getMethod(methodName, BaseRequest.class).getAnnotation(RoutePath.class);

      if (ann != null) {
        return ann.value();
      }
    } catch (NoSuchMethodException e) {
      log.debug("Class {} does not override {}", this.getClass(), methodName);
    }

    return "/";
  }
}
