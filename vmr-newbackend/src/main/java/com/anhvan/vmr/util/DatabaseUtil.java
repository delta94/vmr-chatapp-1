package com.anhvan.vmr.util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.*;

public class DatabaseUtil {
  public static Future<RowSet<Row>> execute(Query<RowSet<Row>> query) {
    Promise<RowSet<Row>> rowSetPromise = Promise.promise();
    query.execute(
        ar -> {
          if (ar.succeeded()) {
            rowSetPromise.complete(ar.result());
          } else {
            rowSetPromise.fail(ar.cause());
          }
        });
    return rowSetPromise.future();
  }

  public static Future<RowSet<Row>> execute(PreparedQuery<RowSet<Row>> query, Tuple tuple) {
    Promise<RowSet<Row>> rowSetPromise = Promise.promise();
    query.execute(
        tuple,
        ar -> {
          if (ar.succeeded()) {
            rowSetPromise.complete(ar.result());
          } else {
            rowSetPromise.fail(ar.cause());
          }
        });
    return rowSetPromise.future();
  }
}
