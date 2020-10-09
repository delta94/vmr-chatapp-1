package com.anhvan.vmr.database;

import com.anhvan.vmr.config.DatabaseConfig;
import com.anhvan.vmr.entity.FutureStateHolder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class DatabaseService {
  public static final String CONN_KEY = "conn";

  public static final String TX_KEY = "tx";

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

  /**
   * @param futureStateHolder initial state holder
   * @return state holder after set transaction
   */
  public Future<FutureStateHolder> getTransaction(FutureStateHolder futureStateHolder) {
    Promise<FutureStateHolder> promise = Promise.promise();
    pool.getConnection(
        ar -> {
          if (ar.succeeded()) {
            SqlConnection connection = ar.result();
            Transaction transaction = connection.begin();
            futureStateHolder.set(CONN_KEY, connection);
            futureStateHolder.set(TX_KEY, transaction);
            promise.complete(futureStateHolder);
          } else {
            promise.fail(ar.cause());
          }
        });
    return promise.future();
  }
}
