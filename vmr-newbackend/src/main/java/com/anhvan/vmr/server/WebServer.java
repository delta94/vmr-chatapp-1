package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Log4j2
public class WebServer {
  private Vertx vertx;
  private ServerConfig config;
  private RouterFactory routerFactory;

  public void start() {
    log.info("Create web server at port {}", config.getPort());
    vertx
        .createHttpServer()
        .requestHandler(routerFactory.route())
        .exceptionHandler(throwable -> log.error("An exception occur when start server", throwable))
        .listen(config.getPort(), config.getHost());
  }
}