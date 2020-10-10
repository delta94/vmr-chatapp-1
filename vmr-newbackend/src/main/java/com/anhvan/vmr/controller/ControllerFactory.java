package com.anhvan.vmr.controller;

import io.vertx.ext.web.Router;

public interface ControllerFactory {
  void registerControllers(Router router);
}
