package com.anhvan.vmr.server;

import com.anhvan.vmr.handler.HandlerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

import java.util.HashSet;
import java.util.Set;

public class RouterFactory {
  public static Router route(Vertx vertx, JWTAuth auth) {
    Router router = Router.router(vertx);

    setHeaderAllowances(router);

    router.route().handler(BodyHandler.create());

    router.route("/api/protected/*").handler(JWTAuthHandler.create(auth));

    HandlerFactory.init(router);

    return router;
  }

  private static void setHeaderAllowances(Router router) {
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
