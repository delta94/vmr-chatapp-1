package com.anhvan.vmr.server;

import io.vertx.reactivex.ext.web.Router;

public interface HttpApiHandler {
  Router getRouter();
}
