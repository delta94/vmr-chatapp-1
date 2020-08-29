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
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class UserController implements Controller {
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
    HttpServerResponse res = routingContext.response();
    res.putHeader("Content-Type", "application/json");
    JsonObject result = new JsonObject();

    Future<List<User>> cachedList = userCacheService.getUserList();
    cachedList.onComplete(
        listAsyncResult -> {
          if (listAsyncResult.succeeded()) {
            log.trace("Cache hit");
            ControllerUtil.jsonResponse(res, result.put("userList", listAsyncResult.result()));
          } else {
            log.trace("Cache miss");
            Future<List<User>> userListFuture = userDBService.getListUser();
            userListFuture.onSuccess(
                users -> {
                  JsonObject jsonResponse = new JsonObject().put("userList", users);
                  userCacheService.setUserList(users);
                  res.end(jsonResponse.toBuffer());
                });
          }
        });
  }
}
