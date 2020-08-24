package com.anhvan.vmr.verticles;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerVerticle extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(ServerVerticle.class);
  private final JsonObject conf;

  public ServerVerticle(JsonObject conf) {
    super();
    this.conf = conf;
  }

  @Override
  public void start() {
    vertx
        .createHttpServer()
        .requestHandler(
            request -> {
              HttpServerResponse response = request.response();
              response.end("Hello world");
            })
        .listen(
            conf.getInteger("port"),
            conf.getString("host"),
            httpServerAsyncResult -> {
              if (httpServerAsyncResult.succeeded()) {
                logger.info("Webserver start at port {}", conf.getInteger("port"));
              }
            });
  }

  @Override
  public void stop() {
    System.out.println("Stop");
  }
}
