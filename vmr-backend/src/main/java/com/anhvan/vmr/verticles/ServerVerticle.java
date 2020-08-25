package com.anhvan.vmr.verticles;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(ServerVerticle.class);

  private final JsonObject conf;

  public ServerVerticle(JsonObject conf) {
    super();
    this.conf = conf;
  }

  @Override
  public void start() {
    String host = conf.getString("host");
    int port = conf.getInteger("port");

    Single<HttpServer> httpServerSingle =
        vertx
            .createHttpServer()
            .requestHandler(
                request -> {
                  HttpServerResponse response = request.response();
                  response.end("Hello world");
                })
            .rxListen(port, host);

    httpServerSingle
        .subscribe(
            server -> LOGGER.info("Server start at {}:{}", host, port),
            failue -> {
              LOGGER.error("Error when create http server", failue);
              vertx.close();
            })
        .isDisposed();
  }

  @Override
  public void stop() {
    LOGGER.info("Server is stopped");
  }
}
