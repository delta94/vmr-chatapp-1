package com.anhvan.vmr.server;

import com.anhvan.vmr.configs.ServerConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpServer;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class WebSocketServer extends AbstractVerticle {
  private ServerConfig config;
  private WebSocketFactory webSocketFactory;

  @Inject
  public WebSocketServer(ServerConfig config, WebSocketFactory webSocketFactory) {
    this.config = config;
    this.webSocketFactory = webSocketFactory;
  }

  public void start() {
    String wsHost = config.getHost();
    int wsPort = config.getWsPort();
    vertx
        .createHttpServer()
        .webSocketHandler(webSocketFactory::handleWebSocketConn)
        .listen(
            wsPort,
            wsHost,
            serverAsyncResult -> handleAfterBind(serverAsyncResult, wsHost, wsPort));
  }

  private void handleAfterBind(
      AsyncResult<HttpServer> serverAsyncResult, String wsHost, int wsPort) {
    if (serverAsyncResult.succeeded()) {
      log.info("Start websocket server at  {}:{}", wsHost, wsPort);
    } else {
      log.error(
          "Fails to start websocket server at {}:{}", wsHost, wsPort, serverAsyncResult.cause());
    }
  }
}
