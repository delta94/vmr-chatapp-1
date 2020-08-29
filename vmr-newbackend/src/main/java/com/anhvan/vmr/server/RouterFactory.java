package com.anhvan.vmr.server;

import com.anhvan.vmr.cache.TokenCacheService;
import com.anhvan.vmr.controller.ControllerFactory;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Log4j2
public class RouterFactory {
  private Vertx vertx;
  private JWTAuth jwtAuth;
  private ControllerFactory controllerFactory;
  private JwtUtil jwtUtil;
  private TokenCacheService tokenCacheService;

  @Inject
  public RouterFactory(
      Vertx vertx,
      JWTAuth auth,
      ControllerFactory controllerFactory,
      JwtUtil jwtUtil,
      TokenCacheService tokenCacheService) {
    this.vertx = vertx;
    this.jwtAuth = auth;
    this.controllerFactory = controllerFactory;
    this.jwtUtil = jwtUtil;
    this.tokenCacheService = tokenCacheService;
  }

  public Router route() {
    Router router = Router.router(vertx);

    setHeaderAllowances(router);

    // Parse body to json
    router.route().handler(BodyHandler.create());

    // Protected api
    router
        .route("/api/protected/*")
        .handler(JWTAuthHandler.create(jwtAuth))
        .handler(this::jwtLogoutHandler);

    // Register controller
    controllerFactory.registerController(router);

    // Exception handling
    router
        .route()
        .failureHandler(
            routingContext -> {
              log.error("An exception occur when handing request", routingContext.failure());
              routingContext.next();
            });

    return router;
  }

  private void jwtLogoutHandler(RoutingContext routingContext) {
    String jwtToken = jwtUtil.getTokenFromHeader(routingContext);

    Future<Boolean> existFuture = tokenCacheService.checkExistInBacklist(jwtToken);

    existFuture.onSuccess(
        exist -> {
          if (exist) {
            routingContext.response().setStatusCode(401).end();
          } else {
            routingContext.next();
          }
        });

    existFuture.onFailure(
        throwable -> {
          log.error("Error when check if token in blacklist", throwable);
          routingContext.next();
        });
  }

  private void setHeaderAllowances(Router router) {
    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("accept");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("Authorization");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.OPTIONS);
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.PUT);

    router
        .route("/*")
        .handler(
            CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods))
        .handler(BodyHandler.create());
  }
}
