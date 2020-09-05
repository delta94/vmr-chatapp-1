package com.anhvan.vmr.controller;

import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class BaseController implements Controller {
  @Override
  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route("/").handler(this::handle);
    return router;
  }

  protected void handle(RoutingContext routingContext) {
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

    // Create request handler
    Future<BaseResponse> requestHandler;

    // Choose handler
    switch (request.method()) {
      case GET:
        requestHandler = handleGet(baseRequest);
        break;
      case POST:
        requestHandler = handlePost(baseRequest);
        break;
      case PUT:
        requestHandler = handlePut(baseRequest);
        break;
      case DELETE:
        requestHandler = handleDelete(baseRequest);
        break;
      case PATCH:
        requestHandler = handlePatch(baseRequest);
        break;
      default:
        requestHandler = handleNotSupported();
    }

    // If request handler is not implemented
    if (requestHandler == null) {
      requestHandler = handleNotSupported();
    }

    // Handle request
    requestHandler.onComplete(
        rs -> {
          if (rs.succeeded()) {
            BaseResponse handlerResponse = rs.result();
            response
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .setStatusCode(handlerResponse.getStatusCode())
                .end(Json.encodeToBuffer(handlerResponse));
          } else {
            log.error("Error when handle request {}", request, rs.cause());
            response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
          }
        });
  }

  protected Future<BaseResponse> handleGet(BaseRequest baseRequest) {
    return null;
  }

  protected Future<BaseResponse> handlePost(BaseRequest baseRequest) {
    return null;
  }

  protected Future<BaseResponse> handlePut(BaseRequest baseRequest) {
    return null;
  }

  protected Future<BaseResponse> handleDelete(BaseRequest baseRequest) {
    return null;
  }

  protected Future<BaseResponse> handlePatch(BaseRequest baseRequest) {
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
}
