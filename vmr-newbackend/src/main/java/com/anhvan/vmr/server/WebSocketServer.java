package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class WebSocketServer {
  private Vertx vertx;
  private ServerConfig config;

  @Inject
  public WebSocketServer(Vertx vertx, ServerConfig config) {
    this.vertx = vertx;
    this.config = config;
  }

  public void start() {
    int port = config.getPort() + 1;
    vertx
        .createHttpServer()
        .webSocketHandler(this::websocketHandler)
        .listen(
            port,
            config.getHost(),
            result -> {
              if (result.succeeded()) {
                log.info("Start websocket server at port {}", port);
              }
            });
  }

  private void websocketHandler(ServerWebSocket webSocket) {
    System.out.println(webSocket.headers());
    webSocket.accept();
  }
}
