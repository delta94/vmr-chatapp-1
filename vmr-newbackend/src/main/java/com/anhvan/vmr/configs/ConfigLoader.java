package com.anhvan.vmr.configs;

import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Log4j2
public class ConfigLoader {
  public static JsonObject loadConfig() {
    Yaml yaml = new Yaml();

    InputStream inputStream =
        ConfigLoader.class.getClassLoader().getResourceAsStream("configuration.yml");

    try {
      String configPath = System.getProperty("vmr-config-file");
      if (configPath != null) {
        inputStream = Files.newInputStream(Paths.get(configPath));
      }
    } catch (IOException e) {
      log.error("Error when load configuration file, use default config", e);
    }

    Map<String, Object> config = yaml.load(inputStream);
    JsonObject jsonConfig = JsonObject.mapFrom(config);

    log.debug("Load configuration successfully {}", jsonConfig);

    return jsonConfig;
  }
}
