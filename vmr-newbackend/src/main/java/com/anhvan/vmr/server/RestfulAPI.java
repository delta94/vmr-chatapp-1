package com.anhvan.vmr.server;

import com.anhvan.vmr.config.RestfulAPIConfig;
import io.vertx.core.Vertx;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor
public class RestfulAPI {
  private static final Logger LOGGER = LogManager.getLogger(RestfulAPI.class);

  private final Vertx vertx;
  private final RestfulAPIConfig config;

  public void start() {
    vertx
        .createHttpServer()
        .requestHandler(
            httpServerRequest -> {
              httpServerRequest.response().end("Hi");
            })
        .listen(config.getPort(), config.getHost());
  }
}
