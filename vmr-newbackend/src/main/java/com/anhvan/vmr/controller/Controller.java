package com.anhvan.vmr.controller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public interface Controller {
  Router getRouter(Vertx vertx);
}
