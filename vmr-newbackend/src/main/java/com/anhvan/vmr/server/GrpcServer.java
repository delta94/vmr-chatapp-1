package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import com.anhvan.vmr.grpc.AuthInterceptor;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;

@Singleton
@Log4j2
public class GrpcServer {
  private Server grpcServer;
  private int port;

  @Inject
  public GrpcServer(
      ServerConfig serverConfig, Set<BindableService> serviceSet, AuthInterceptor authInterceptor) {
    // Get port
    port = serverConfig.getGrpcPort();

    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);

    // Bind service
    for (BindableService service : serviceSet) {
      serverBuilder.addService(service);
    }

    // Set authentication interceptor
    serverBuilder.intercept(authInterceptor);

    // Set thread pool size
    serverBuilder.executor(Executors.newFixedThreadPool(40));

    grpcServer = serverBuilder.build();
  }

  public void start() {
    try {
      grpcServer.start();
      log.info("Start grpc server at port {}", port);
    } catch (IOException e) {
      log.error("Error when create grpc server", e);
    }
  }
}
