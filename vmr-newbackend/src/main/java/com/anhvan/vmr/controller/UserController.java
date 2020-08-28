package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.ControllerUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@AllArgsConstructor
@Builder
public class UserController implements Controller {
  private final Logger LOGGER = LogManager.getLogger(UserController.class);

  private Vertx vertx;
  private UserDBService userDBService;
  private UserCacheService userCacheService;

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
