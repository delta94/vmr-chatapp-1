package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class WebServer {
  private Vertx vertx;
  private ServerConfig config;
  private RouterFactory routerFactory;

  @Inject
  public WebServer(
      Vertx vertx,
      ServerConfig config,
      RouterFactory routerFactory) {
    this.vertx = vertx;
    this.config = config;
    this.routerFactory = routerFactory;
  }

  public void start() {
    log.info("Create web server at port {}", config.getPort());

    vertx
        .createHttpServer(
            new HttpServerOptions()
                .setTcpKeepAlive(true)
                .setMaxHeaderSize(32 * 1024)
                .setLogActivity(true))
        .requestHandler(routerFactory.route())
        .exceptionHandler(throwable -> log.error("An exception occur when start server", throwable))
        .listen(config.getPort(), config.getHost());
  }
}
