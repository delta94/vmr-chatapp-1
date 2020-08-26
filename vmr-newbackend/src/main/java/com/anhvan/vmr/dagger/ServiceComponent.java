package com.anhvan.vmr.dagger;

import com.anhvan.vmr.server.RestfulAPI;
import dagger.BindsInstance;
import dagger.Component;
import io.vertx.core.Vertx;

import javax.inject.Singleton;

@Component(modules = {ServiceModule.class})
@Singleton
public interface ServiceComponent {
  Vertx getVertx();

  RestfulAPI getRestfulAPI();

  @Component.Builder
  interface Builder {
    ServiceComponent build();

    Builder serviceModule(ServiceModule module);
  }
}
