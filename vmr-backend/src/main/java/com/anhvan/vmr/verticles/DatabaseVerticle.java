package com.anhvan.vmr.verticles;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.mysqlclient.MySQLPool;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.reactivex.sqlclient.RowSet;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.sqlclient.PoolOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(DatabaseVerticle.class);

  private final JsonObject config;
  private final CompositeDisposable compositeDisposable;
  private MySQLPool clientPool;

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

    PoolOptions poolOptions = new PoolOptions().setMaxSize(config.getInteger("poolSize", 10));

    // Create the client pool
    clientPool = MySQLPool.pool(vertx, connectOptions, poolOptions);
    sample();
  }

  @Override
  public void stop() {
    compositeDisposable.dispose();
    LOGGER.info("Stop mysql verticle");
  }

  private void sample() {
    Disposable disposable =
        clientPool
            .rxGetConnection()
            .subscribe(
                conn -> {
                  LOGGER.info("Connect to mysql sucessfully");
                  handle(conn);
                },
                err -> {
                  LOGGER.error("Error when connect to MySQL", err);
                  vertx.close();
                });

    compositeDisposable.add(disposable);
  }

  private void handle(SqlConnection conn) {
    // TODO
    conn.query("show tables;")
        .execute(
            rowSetAsyncResult -> {
              if (rowSetAsyncResult.succeeded()) {
                RowSet<Row> result = rowSetAsyncResult.result();
                System.out.println(result.size());
                result.forEach(row -> System.out.println(row.getString(0)));
              }
              conn.close();
            });
  }
}
