package com.anhvan.vmr.server;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterHandler implements HttpApiHandler {
  private static final Logger LOGGER = LogManager.getLogger(RegisterHandler.class);

  private final Router router;
  private final Vertx vertx;

  public RegisterHandler(Vertx vertx) {
    router = Router.router(vertx);
    this.vertx = vertx;
  }

  @Override
  public Router getRouter() {
    router.post("/").handler(this::registerHandler);
    return router;
  }

  private void registerHandler(RoutingContext routingContext) {
    EventBus eventBus = vertx.eventBus();
    HttpServerRequest request = routingContext.request();
    HttpServerResponse response = routingContext.response();
    request.bodyHandler(
        buffer -> {
          JsonObject userInfo = buffer.toJsonObject();
          // Save to database
          eventBus
              .rxRequest("db.user.register", userInfo)
              .subscribe(
                  msg -> {
                    JsonObject result = (JsonObject) msg.body();
                    if (result.getString("status").equals("exist")) {
                      response.setStatusCode(409).end();
                    } else {
                      int userId = result.getInteger("userId");
                      eventBus.request(
                          "service.jwt.create",
                          result,
                          jwtResult -> {
                            JsonObject jsonJwt = (JsonObject) jwtResult.result().body();
                            JsonObject jsonResponse = new JsonObject();
                            jsonResponse.put("userId", userId);
                            jsonResponse.put("jwtToken", jsonJwt.getValue("jwtToken"));
                            response.setStatusCode(201);
                            response.end(jsonResponse.toString());
                          });
                    }
                  },
                  failue -> {
                    LOGGER.info("Exception occur", failue);
                    response.setStatusCode(400).end();
                  })
              .isDisposed();
        });
  }
}
