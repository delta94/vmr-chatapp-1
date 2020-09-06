package com.anhvan.vmr.database;

import com.anhvan.vmr.config.DatabaseConfig;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
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

    // Test connection
    pool.query("show tables")
        .execute(
            rowSet -> {
              if (rowSet.failed()) {
                log.fatal("Cannot connect to mysql", rowSet.cause());
                vertx.close();
              } else {
                log.info("Connect to mysql successfully");
              }
            });
  }

  public MySQLPool getPool() {
    return pool;
  }
}
