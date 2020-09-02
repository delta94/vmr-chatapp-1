package com.anhvan.vmr.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfigLoader {
  private final Vertx vertx;

  public Future<JsonObject> load() {
    Promise<JsonObject> configurations = Promise.promise();

    // Default config
    ConfigStoreOptions fileStoreOptions =
        new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", "configuration.yml"));

    // Load config
    ConfigRetrieverOptions retrieverOptions =
        new ConfigRetrieverOptions().addStore(fileStoreOptions);

    // External config
    String externalConfigFile = System.getProperty("vmr-config-file");
    if (externalConfigFile != null) {
      ConfigStoreOptions externalStoreOptions =
          new ConfigStoreOptions()
              .setType("file")
              .setOptional(true)
              .setFormat("yaml")
              .setConfig(new JsonObject().put("path", externalConfigFile));
      retrieverOptions.addStore(externalStoreOptions);
    }

    // Handler
    ConfigRetriever.create(vertx, retrieverOptions)
        .getConfig(
            configHandler -> {
              if (configHandler.succeeded()) {
                configurations.complete(configHandler.result());
              } else {
                configurations.fail("Cannot load configuration");
              }
            });

    return configurations.future();
  }
}
