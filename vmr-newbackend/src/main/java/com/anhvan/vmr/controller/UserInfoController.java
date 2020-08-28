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

@AllArgsConstructor
@Builder
public class UserInfoController implements Controller {
  private static final Logger LOGGER = LogManager.getLogger(UserInfoController.class);

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
    LOGGER.debug(principal);
    Future<User> userInfo = userCacheService.getUserCache(userId);
    userInfo.onSuccess(
        user -> {
          LOGGER.debug("Cache hit");
          ControllerUtil.jsonResponse(response, JsonObject.mapFrom(user), 200);
        });
    userInfo.onFailure(
        failue -> {
          LOGGER.debug("Cache miss");
          userDBService
              .getUserById(userId)
              .onSuccess(
                  user -> {
                    ControllerUtil.jsonResponse(response, JsonObject.mapFrom(user));
                    userCacheService.setUserCache(user);
                  })
              .onFailure(
                  throwable -> {
                    LOGGER.info("Cannot get authenticated user", throwable);
                    response.setStatusCode(404).end();
                  });
        });
  }
}
