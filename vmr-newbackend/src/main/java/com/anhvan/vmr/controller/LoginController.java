package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import com.anhvan.vmr.util.ControllerUtil;
import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jodd.crypt.BCrypt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

@Builder
@AllArgsConstructor
@Log4j2
public class LoginController implements Controller {
  private Vertx vertx;
  private UserDBService userDBService;
  private JwtUtil jwtUtil;
  private UserCacheService userCacheService;
  private AsyncWorkerUtil workerUtil;

  @Override
  public Router getRouter() {
    Router result = Router.router(vertx);
    result.post("/").handler(this::handleLogin);
    return result;
  }

  public void handleLogin(RoutingContext context) {
    JsonObject body = context.getBodyAsJson();
    HttpServerResponse response = context.response();
    User user = body.mapTo(User.class);

    Future<User> userFuture = userDBService.getUserByUsername(user.getUsername());
    userFuture.onSuccess(
        dbUser ->
            workerUtil.execute(
                () -> {
                  if (BCrypt.checkpw(user.getPassword(), dbUser.getPassword())) {
                    jwtUtil
                        .generate(dbUser.getId())
                        .onSuccess(
                            token -> {
                              JsonObject res =
                                  new JsonObject()
                                      .put("jwtToken", token)
                                      .put("userId", dbUser.getId());
                              ControllerUtil.jsonResponse(response, res);
                            });
                    userCacheService.setUserCache(dbUser);
                  } else {
                    response.setStatusCode(401).end();
                  }
                }));

    userFuture.onFailure(throwable -> response.setStatusCode(401).end());
  }
}
