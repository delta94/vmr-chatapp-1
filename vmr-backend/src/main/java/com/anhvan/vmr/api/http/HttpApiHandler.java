package com.anhvan.vmr.api.http;

import io.vertx.reactivex.ext.web.Router;

public interface HttpApiHandler {
  Router getRouter();
}
