package com.anhvan.vmr.verticle;

import com.anhvan.vmr.database.RegisterUserService;
import io.reactivex.disposables.CompositeDisposable;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(DatabaseVerticle.class);

  private final JsonObject config;
  private final CompositeDisposable compositeDisposable;

  public DatabaseVerticle(JsonObject config) {
    this.config = config;
    compositeDisposable = new CompositeDisposable();
  }

  @Override
  public void start() {
    MySQLConnectOptions connectOptions =
        new MySQLConnectOptions()
            .setHost(config.getString("host", "127.0.0.1"))
            .setPort(config.getInteger("port", 3306))
            .setUser(config.getString("username", "root"))
            .setPassword(config.getString("password", "password"))
            .setDatabase(config.getString("database", "vmrchat"))
            .setCharset("utf8mb4")
            .setCollation("utf8mb4_unicode_ci");

    // Pool options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(config.getInteger("poolSize", 10));

    // Create the client pool
    MySQLPool clientPool = MySQLPool.pool(vertx, connectOptions, poolOptions);

    EventBus eventBus = vertx.eventBus();

    eventBus.consumer("db.user.register", new RegisterUserService(clientPool));
  }

  @Override
  public void stop() {
    compositeDisposable.dispose();
    LOGGER.info("Stop mysql verticle");
  }
}
