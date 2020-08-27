package com.anhvan.vmr.controller;

import io.vertx.ext.web.Router;

import javax.inject.Inject;

public class ControllerFactory {
  private IndexController indexController;
  private RegisterController registerController;
  private LoginController loginController;
  private UserController userController;

  @Inject
  public ControllerFactory(
      IndexController indexController,
      RegisterController registerController,
      LoginController loginController,
      UserController userController) {
    this.indexController = indexController;
    this.registerController = registerController;
    this.loginController = loginController;
    this.userController = userController;
  }

  public void registerController(Router router) {
    router.mountSubRouter("/", indexController.getRouter());
    router.mountSubRouter("/api/public/register", registerController.getRouter());
    router.mountSubRouter("/api/public/login", loginController.getRouter());
    router.mountSubRouter("/api/protected/users", userController.getRouter());
  }
}
