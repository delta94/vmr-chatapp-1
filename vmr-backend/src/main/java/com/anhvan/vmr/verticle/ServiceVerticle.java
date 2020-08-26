package com.anhvan.vmr.verticle;

import com.anhvan.vmr.util.JwtUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

public class ServiceVerticle extends AbstractVerticle {
  private final JsonObject config;

  public ServiceVerticle(JsonObject conf) {
    config = conf;
  }

  @Override
  public void start() {
    createTokenListener();
  }

  private void createTokenListener() {
    JwtUtil jwtUtil = new JwtUtil(config.getString("jwtSecret"));
    EventBus eventBus = vertx.eventBus();
    eventBus.consumer(
        "service.jwt.create",
        message -> {
          JsonObject userInfo = (JsonObject) message.body();
          System.out.println(userInfo);
          vertx.executeBlocking(
              promise -> promise.complete(jwtUtil.genKey(userInfo.getInteger("userId"))),
              asyncResult -> message.reply(new JsonObject().put("jwtToken", asyncResult.result())));
        });
  }
}
