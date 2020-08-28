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

@AllArgsConstructor
@Builder
@Log4j2
public class UserInfoController implements Controller {
  private Vertx vertx;
  private UserDBService userDBService;
  private UserCacheService userCacheService;

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router.get("/").handler(this::getUserInfoHandler);
    return router;
  }

  private void getUserInfoHandler(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    JsonObject principal = routingContext.user().principal();
    int userId = principal.getInteger("userId");
    log.debug(principal);
    Future<User> userInfo = userCacheService.getUserCache(userId);
    userInfo.onSuccess(
        user -> {
          log.debug("Cache hit");
          ControllerUtil.jsonResponse(response, JsonObject.mapFrom(user), 200);
        });
    userInfo.onFailure(
        failue -> {
          log.debug("Cache miss");
          userDBService
              .getUserById(userId)
              .onSuccess(
                  user -> {
                    ControllerUtil.jsonResponse(response, JsonObject.mapFrom(user));
                    userCacheService.setUserCache(user);
                  })
              .onFailure(
                  throwable -> {
                    log.info("Cannot get authenticated user", throwable);
                    response.setStatusCode(404).end();
                  });
        });
  }
}
