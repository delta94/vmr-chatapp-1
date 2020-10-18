package com.anhvan.vmr.server;

import com.anhvan.vmr.configs.ServerConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class WebServerVerticle extends AbstractVerticle {
  private ServerConfig config;
  private RouterFactory routerFactory;

  @Inject
  public WebServerVerticle(ServerConfig config, RouterFactory routerFactory) {
    this.config = config;
    this.routerFactory = routerFactory;
  }

  @Override
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
