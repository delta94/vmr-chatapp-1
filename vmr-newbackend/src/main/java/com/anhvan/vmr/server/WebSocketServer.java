package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;

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
        .webSocketHandler(this::websocketHandler)
        .listen(config.getPort(), config.getHost());
  }

  private void websocketHandler(ServerWebSocket webSocket) {
    System.out.println(webSocket.headers());
    webSocket.accept();
  }
}
