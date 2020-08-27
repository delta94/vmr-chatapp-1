package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;

import javax.inject.Inject;

public class WebSocketServer {
  private Vertx vertx;
  private ServerConfig config;

  @Inject
  public WebSocketServer(Vertx vertx, ServerConfig config) {
    this.vertx = vertx;
    this.config = config;
  }

  public void start() {
    vertx
        .createHttpServer()
        .webSocketHandler(ws -> System.out.println("Connect"))
        .listen(config.getPort(), config.getHost());
  }
}
