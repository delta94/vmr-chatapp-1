package com.anhvan.vmr.server;

import com.anhvan.vmr.config.ServerConfig;
import com.anhvan.vmr.grpc.SampleServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.vertx.core.AbstractVerticle;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
@Log4j2
public class GrpcServer extends AbstractVerticle {
  private Server grpcServer;
  private int port;

  @Inject
  public GrpcServer(ServerConfig serverConfig, SampleServiceImpl sampleService) {
    port = serverConfig.getGrpcPort();
    grpcServer = ServerBuilder.forPort(port).addService(sampleService).build();
  }

  @Override
  public void start() {
    try {
      grpcServer.start();
      log.info("Start grpc server at port {}", port);
    } catch (IOException e) {
      log.error("Error when create grpc server", e);
    }
  }
}
