package com.anhvan.vmr.utils;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;

public class ConfigLoader {
  public static ConfigRetriever load(Vertx vertx) {
    ConfigStoreOptions externalStoreOptions =
        new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setFormat("yaml")
            .setConfig(
                new JsonObject().put("path", System.getProperty("vmr-config-file", "conf.yml")));

    ConfigStoreOptions fileStoreOptions =
        new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setFormat("yaml")
            .setConfig(new JsonObject().put("path", "conf.yml"));

    ConfigRetrieverOptions retrieverOptions =
        new ConfigRetrieverOptions().addStore(fileStoreOptions).addStore(externalStoreOptions);

    return ConfigRetriever.create(vertx, retrieverOptions);
  }
}
