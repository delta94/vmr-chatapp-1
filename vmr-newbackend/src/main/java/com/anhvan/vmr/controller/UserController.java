package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.ControllerUtil;
import com.anhvan.vmr.websocket.WebSocketService;
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
import java.util.Set;

@AllArgsConstructor
@Builder
@Log4j2
public class UserController implements Controller {
  private Vertx vertx;
  private UserDBService userDBService;
  private UserCacheService userCacheService;
  private WebSocketService webSocketService;

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);

    router.get("/").handler(this::userList);

    return router;
  }

  private void userList(RoutingContext routingContext) {
    HttpServerResponse res = routingContext.response();
    res.putHeader("Content-Type", "application/json");

    // Get users from cache
    Future<List<User>> cachedList = userCacheService.getUserList();
    cachedList.onComplete(
        listAsyncResult -> {
          JsonObject result = new JsonObject();
          if (listAsyncResult.succeeded()) {
            // Cache hit
            log.trace("Cache hit");
            List<User> userList = listAsyncResult.result();
            setOnline(userList);
            result.put("userList", userList);
            ControllerUtil.jsonResponse(res, result);
          } else {
            // Cache miss
            log.trace("Cache miss");

            // Load user form database
            Future<List<User>> userListFuture = userDBService.getListUser();
            userListFuture.onSuccess(
                users -> {
                  setOnline(users);
                  result.put("userList", users);
                  userCacheService.setUserList(users);
                  ControllerUtil.jsonResponse(res, result);
                });
          }
        });
  }

  public void setOnline(List<User> userList) {
    Set<Integer> onlineSet = webSocketService.getOnlineIds();
    userList.forEach(user -> user.setOnline(onlineSet.contains(user.getId())));
  }
}
