package com.anhvan.vmr.database;

import com.anhvan.vmr.config.DatabaseConfig;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

import javax.inject.Inject;

public class DatabaseService {
  private MySQLPool pool;

  @Inject
  public DatabaseService(Vertx vertx, DatabaseConfig config) {
    // Connect options
    MySQLConnectOptions connectOptions =
        new MySQLConnectOptions()
            .setHost(config.getHost())
            .setPort(config.getPort())
            .setUser(config.getUsername())
            .setPassword(config.getPassword())
            .setDatabase(config.getDatabase())
            .setCharacterEncoding("utf8")
            .setCharset("utf8mb4")
            .setCollation("utf8mb4_unicode_ci");

    // Pool options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(config.getPoolSize());

    // Create connection pool
    pool = MySQLPool.pool(vertx, connectOptions, poolOptions);
  }

  public MySQLPool getPool() {
    return pool;
  }
}
