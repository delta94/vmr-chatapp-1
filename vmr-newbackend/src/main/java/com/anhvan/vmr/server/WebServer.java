package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebServer {
  private static final Logger LOGGER = LogManager.getLogger(WebServer.class);

  private Vertx vertx;
  private ServerConfig config;
  private JWTAuth jwtAuth;

  public void start() {
    LOGGER.info("Create web server at port {}", config.getPort());
    vertx
        .createHttpServer()
        .requestHandler(RouterFactory.route(vertx, jwtAuth))
        .listen(config.getPort(), config.getHost());
  }
}
