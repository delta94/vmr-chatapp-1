package com.anhvan.vmr.database;

import com.anhvan.vmr.config.DatabaseConfig;
import com.anhvan.vmr.entity.FutureStateHolder;
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
            .setCollation("utf8mb4_unicode_ci")
            .setConnectTimeout(config.getTimeout());

    // Pool options
    PoolOptions poolOptions =
        new PoolOptions()
            .setMaxSize(config.getPoolSize())
            .setMaxWaitQueueSize(config.getQueueSize());

    // Create connection pool
    pool = MySQLPool.pool(vertx, connectOptions, poolOptions);

    // Log
    log.info("Create mysql connection pool");
  }

  public MySQLPool getPool() {
    return pool;
  }

  public TransactionManager getTransactionManager(FutureStateHolder futureStateHolder) {
    return new TransactionManager(pool, futureStateHolder);
  }

  public TransactionManager getTransactionManager() {
    return getTransactionManager(new FutureStateHolder());
  }
}
