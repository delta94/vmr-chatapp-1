package com.anhvan.vmr.controller;

import io.vertx.ext.web.Router;

import javax.inject.Inject;
import java.util.Map;

public class ControllerFactory {
  private Map<String, Controller> controllerMap;

  @Inject
  public ControllerFactory(Map<String, Controller> controllerMap) {
    this.controllerMap = controllerMap;
  }

  public void registerController(Router router) {
    for (Map.Entry<String, Controller> controllerEntry : controllerMap.entrySet()) {
      router.mountSubRouter(controllerEntry.getKey(), controllerEntry.getValue().getRouter());
    }
  }
}
