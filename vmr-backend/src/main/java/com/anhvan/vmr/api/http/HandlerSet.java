package com.anhvan.vmr.api.http;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

public class HandlerSet implements HttpApiHandler {
  private final Router router;
  private final Vertx vertx;

  public HandlerSet(Vertx vertx) {
    router = Router.router(vertx);
    this.vertx = vertx;
  }

  private void registerHandler(String path, HttpApiHandler handler) {
    router.mountSubRouter(path, handler.getRouter());
  }

  public void registerHandler() {
    registerHandler("/register", new RegisterHandler(vertx));
  }

  @Override
  public Router getRouter() {
    registerHandler();
    return router;
  }

  public static Router getRouter(Vertx vertx) {
    return new HandlerSet(vertx).getRouter();
  }
}
