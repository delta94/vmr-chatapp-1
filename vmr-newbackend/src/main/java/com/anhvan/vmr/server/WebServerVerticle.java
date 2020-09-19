package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class WebServer {
  private Vertx vertx;
  private ServerConfig config;
  private RouterFactory routerFactory;

  @Inject
  public WebServer(Vertx vertx, ServerConfig config, RouterFactory routerFactory) {
    this.vertx = vertx;
    this.config = config;
    this.routerFactory = routerFactory;
  }

  public void start() {
    String host = config.getHost();
    int port = config.getPort();

    HttpServerOptions httpServerOptions =
        new HttpServerOptions()
            .setTcpKeepAlive(true)
            .setMaxHeaderSize(32 * 1024)
            .setLogActivity(true);

    Handler<AsyncResult<HttpServer>> listenHandler =
        serverAsyncResult -> {
          if (serverAsyncResult.succeeded()) {
            log.info("Server start at {}:{}", host, port);
          } else {
            log.error("Fails to start web server at {}:{}", host, port);
          }
        };

    vertx
        .createHttpServer(httpServerOptions)
        .requestHandler(routerFactory.route())
        .exceptionHandler(throwable -> log.error("An exception occur when start server", throwable))
        .listen(port, host, listenHandler);
  }
}
