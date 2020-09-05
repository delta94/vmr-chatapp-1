package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class WebSocketServer {
  private Vertx vertx;
  private ServerConfig config;
  private WebSocketFactory webSocketFactory;

  @Inject
  public WebSocketServer(Vertx vertx, ServerConfig config, WebSocketFactory webSocketFactory) {
    this.vertx = vertx;
    this.config = config;
    this.webSocketFactory = webSocketFactory;
  }

  public void start() {
    vertx
        .createHttpServer()
        .webSocketHandler(webSocketFactory::webSocketHandler)
        .listen(
            config.getWsPort(),
            config.getHost(),
            result -> {
              if (result.succeeded()) {
                log.info("Start websocket server at port {}", config.getWsPort());
              }
            });
  }
}
