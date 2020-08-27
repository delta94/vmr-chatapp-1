package com.anhvan.vmr.controller;

import io.vertx.ext.web.Router;

import javax.inject.Inject;

public class ControllerFactory {
  private IndexController indexController;
  private RegisterController registerController;

  @Inject
  public ControllerFactory(IndexController indexController, RegisterController registerController) {
    this.indexController = indexController;
    this.registerController = registerController;
  }

  public void registerController(Router router) {
    router.mountSubRouter("/", indexController.getRouter());
    router.mountSubRouter("/api/public/register", registerController.getRouter());
  }
}
