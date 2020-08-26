package com.anhvan.vmr;

import com.anhvan.vmr.config.ConfigLoader;
import com.anhvan.vmr.dagger.DaggerServiceComponent;
import com.anhvan.vmr.dagger.ServiceComponent;
import com.anhvan.vmr.dagger.ServiceModule;
import com.anhvan.vmr.server.RestfulAPI;
import com.anhvan.vmr.utils.Tracker;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;

public class Runner {
  public static void main(String[] args) {
    Vertx vertx =
        Vertx.vertx(
            new VertxOptions()
                .setPreferNativeTransport(true)
                .setMetricsOptions(
                    new MicrometerMetricsOptions()
                        .setMicrometerRegistry(Tracker.getMeterRegistry())
                        .setJvmMetricsEnabled(true)
                        .setPrometheusOptions(
                            new VertxPrometheusOptions()
                                .setEnabled(true)
                                .setStartEmbeddedServer(true)
                                .setPublishQuantiles(true)
                                .setEmbeddedServerOptions(new HttpServerOptions().setPort(8054))
                                .setEmbeddedServerEndpoint("/metrics"))
                        .setEnabled(true)));

    ConfigLoader configLoader = new ConfigLoader(vertx);
    Future<JsonObject> config = configLoader.load();
    config.onComplete(jsonConf -> onLoadConfig(jsonConf.result(), vertx));
  }

  public static void onLoadConfig(JsonObject config, Vertx vertx) {
    ServiceModule module = new ServiceModule(config, vertx);
    ServiceComponent component = DaggerServiceComponent.builder().serviceModule(module).build();
    RestfulAPI restfulAPI = component.getRestfulAPI();
    restfulAPI.start();
  }
}
