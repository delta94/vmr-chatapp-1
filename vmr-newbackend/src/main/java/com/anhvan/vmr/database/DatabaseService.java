package com.anhvan.vmr.database;

import com.anhvan.vmr.config.DatabaseConfig;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class DatabaseService {
  private MySQLPool pool;

  public DatabaseService(DatabaseConfig config) {
    // Connect options
    MySQLConnectOptions connectOptions =
        new MySQLConnectOptions()
            .setHost(config.getHost())
            .setPort(config.getPort())
            .setUser(config.getUsername())
            .setPassword(config.getPassword())
            .setDatabase(config.getDatabase());

    // Pool options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(config.getPoolSize());

    // Create connection pool
    pool = MySQLPool.pool(connectOptions, poolOptions);
  }

  public MySQLPool getPool() {
    return pool;
  }
}
