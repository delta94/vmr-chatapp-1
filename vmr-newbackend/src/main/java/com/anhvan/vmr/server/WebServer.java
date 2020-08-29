package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import com.anhvan.vmr.websocket.WebSocketHandler;
import com.anhvan.vmr.websocket.WebSocketService;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
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
  private WebSocketService webSocketService;

  public void start() {
    log.info("Create web server at port {}", config.getPort());
    vertx
        .createHttpServer()
        .requestHandler(routerFactory.route())
        .exceptionHandler(throwable -> log.error("An exception occur when start server", throwable))
        .webSocketHandler(this::websocketHandler)
        .listen(config.getPort(), config.getHost());
  }

  private void websocketHandler(ServerWebSocket conn) {
    log.trace("Get connection");
    webSocketService
        .authenticate(conn)
        .onComplete(
            userIdRs -> {
              log.trace("Auth status: {}", userIdRs.succeeded());
              if (userIdRs.succeeded()) {
                conn.accept();
                new WebSocketHandler(vertx, conn, userIdRs.result(), webSocketService).handle();
              } else {
                conn.reject();
              }
            });
  }
}
