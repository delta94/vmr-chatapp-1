package com.anhvan.vmr.verticles;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(ServerVerticle.class);

  private final JsonObject conf;
  private final CompositeDisposable compositeDisposable;

  public ServerVerticle(JsonObject conf) {
    super();
    this.conf = conf;
    compositeDisposable = new CompositeDisposable();
  }

  @Override
  public void start() {
    String host = conf.getString("host", "127.0.0.1");
    int port = conf.getInteger("port", 8080);

    Single<HttpServer> httpServerSingle =
        vertx
            .createHttpServer()
            .requestHandler(
                request -> {
                  HttpServerResponse response = request.response();
                  response.end("Hello world");
                })
            .webSocketHandler(
                ws -> {
                  System.out.println(ws.path());
                  ws.accept();
                  long periodicId =
                      vertx.setPeriodic(
                          1000,
                          id -> {
                            ws.writeTextMessage("Hello world");
                          });
                  ws.closeHandler(
                      unused -> {
                        LOGGER.info("CONNECTION CLOSED");
                        vertx.cancelTimer(periodicId);
                      });
                })
            .rxListen(port, host);

    Disposable disposable =
        httpServerSingle.subscribe(
            server -> LOGGER.info("Server start at {}:{}", host, port),
            failue -> {
              LOGGER.error("Error when create http server", failue);
              vertx.close();
            });
    compositeDisposable.add(disposable);
  }

  @Override
  public void stop() {
    LOGGER.info("Server is stopped");
    compositeDisposable.dispose();
  }
}
