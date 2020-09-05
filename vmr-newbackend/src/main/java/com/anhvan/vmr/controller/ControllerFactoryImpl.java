package com.anhvan.vmr.controller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import javax.inject.Inject;
import java.util.Map;

public class ControllerFactoryImpl implements ControllerFactory {
  private Map<String, Controller> controllerMap;
  private Vertx vertx;

  @Inject
  public ControllerFactoryImpl(Map<String, Controller> controllerMap, Vertx vertx) {
    this.controllerMap = controllerMap;
    this.vertx = vertx;
  }

  @Override
  public void registerController(Router router) {
    for (Map.Entry<String, Controller> controllerEntry : controllerMap.entrySet()) {
      router.mountSubRouter(controllerEntry.getKey(), controllerEntry.getValue().getRouter(vertx));
    }
  }
}
