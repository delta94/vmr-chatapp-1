package com.anhvan.vmr.utils;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;

public class ConfigLoader {
  public static ConfigRetriever load(Vertx vertx) {
    // Default config
    ConfigStoreOptions fileStoreOptions =
        new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", ""));

    // External config
    String externalConfigFile = System.getProperty("vmr-config-file", "conf.yml");
    ConfigStoreOptions externalStoreOptions =
        new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", externalConfigFile));

    // Load config
    ConfigRetrieverOptions retrieverOptions =
        new ConfigRetrieverOptions().addStore(fileStoreOptions).addStore(externalStoreOptions);

    // Return
    return ConfigRetriever.create(vertx, retrieverOptions);
  }
}
