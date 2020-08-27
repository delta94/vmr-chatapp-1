package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import java.util.List;

public class UserController implements Controller {
  private Vertx vertx;
  private UserDBService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;

  @Inject
  public UserController(
      Vertx vertx,
      UserDBService userDBService,
      JwtUtil jwtUtil,
      UserCacheService userCacheService) {
    this.vertx = vertx;
    this.userDBService = userDBService;
    this.jwtUtil = jwtUtil;
    this.userCacheService = userCacheService;
  }

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);

    router.get("/").handler(this::userList);

    return router;
  }

  private void userList(RoutingContext routingContext) {
    Future<List<User>> userListFuture = userDBService.getListUser();
    userListFuture.onSuccess(
        users -> {
          JsonObject jsonResponse = new JsonObject().put("userList", users);
          HttpServerResponse res = routingContext.response();
          res.putHeader("Content-Type", "application/json");
          res.end(jsonResponse.toBuffer());
        });
  }
}
