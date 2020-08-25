package com.anhvan.vmr.verticles;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.redis.client.Redis;
import io.vertx.reactivex.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class RedisVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(RedisVerticle.class);

  private final JsonObject conf;

  public RedisVerticle(JsonObject conf) {
    this.conf = conf;
  }

  @Override
  public void start() {
    String connectionString =
        String.format("redis://%s:%d", conf.getString("host"), conf.getInteger("port"));
    RedisOptions redisOptions = new RedisOptions().setConnectionString(connectionString);
    Redis.createClient(vertx, redisOptions)
        .rxConnect()
        .subscribe(
            connection -> {
              LOGGER.info("Connect to redis server successfully");
              RedisAPI redis = RedisAPI.api(connection);
              handle(redis);
            },
            error -> {
              LOGGER.error("Cannot connect to redis", error);
              vertx.close();
            })
        .isDisposed();
  }

  @Override
  public void stop() {
    LOGGER.info("Stop redis verticle");
  }

  public void handle(RedisAPI redis) {
    // TODO
    redis.rxSet(Arrays.asList("name", "Dang Anh Van")).subscribe();
  }
}
