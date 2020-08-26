package com.anhvan.vmr.verticle;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.redis.client.Redis;
import io.vertx.reactivex.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class CacheVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(CacheVerticle.class);

  private final JsonObject conf;
  private final CompositeDisposable compositeDisposable;

  public CacheVerticle(JsonObject conf) {
    this.conf = conf;
    compositeDisposable = new CompositeDisposable();
  }

  @Override
  public void start() {
    // Get host and port
    String host = conf.getString("host", "127.0.0.1");
    int port = conf.getInteger("port", 6379);

    // Connect to redis
    String connectionString = String.format("redis://%s:%d", host, port);
    RedisOptions redisOptions = new RedisOptions().setConnectionString(connectionString);
    Disposable redisDisposable =
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
                });
    compositeDisposable.add(redisDisposable);
  }

  @Override
  public void stop() {
    LOGGER.info("Stop redis verticle");
    compositeDisposable.dispose();
  }

  public void handle(RedisAPI redis) {
    // TODO
    redis.rxSet(Arrays.asList("name", "Dang Anh Van")).subscribe();
  }
}
