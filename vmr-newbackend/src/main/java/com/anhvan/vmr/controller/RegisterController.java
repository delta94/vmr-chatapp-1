package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.RedisCache;
import com.anhvan.vmr.database.DatabaseService;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import jodd.crypt.BCrypt;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;

public class RegisterController implements Controller {
  private Vertx vertx;
  private DatabaseService database;
  private RedisCache cache;
  private JWTAuth auth;

  @Inject
  public RegisterController(Vertx vertx, DatabaseService database, RedisCache cache, JWTAuth auth) {
    this.vertx = vertx;
    this.database = database;
    this.cache = cache;
    this.auth = auth;
  }

  @Override
  public Router getRouter() {
    Router router = Router.router(vertx);
    router.post("/").handler(this::registerUser);
    return router;
  }

  private void registerUser(RoutingContext context) {
    JsonObject requestBody = context.getBodyAsJson();
    HttpServerResponse response = context.response();

    // Get user info
    String username = requestBody.getString("username");
    String password = requestBody.getString("password");
    String name = requestBody.getString("name");

    // Insert into database
    Tuple info = Tuple.of(username, BCrypt.hashpw(password, BCrypt.gensalt()), name);

    // Add user
    Future<Integer> userIdFuture = addUser(info);
    userIdFuture.onFailure(throwable -> response.setStatusCode(409).end());

    // Generate token
    Future<String> tokenFuture = userIdFuture.compose(this::genToken);
    CompositeFuture.all(userIdFuture, tokenFuture)
        .onComplete(
            result -> {
              int userId = result.result().resultAt(0);
              String token = result.result().resultAt(1);
              response.setStatusCode(201);
              response.end(new JsonObject().put("token", token).put("userId", userId).toBuffer());
            });

    // Set cache
    userIdFuture.onSuccess(
        id -> {
          RedissonClient client = cache.getRedissonClient();
          String key = "chatapp:user:" + id.toString() + ":info";
          RMap<String, String> infoMap = client.getMap(key);
          infoMap.put("username", username);
          infoMap.put("name", name);
        });
  }

  private Future<Integer> addUser(Tuple info) {
    Promise<Integer> result = Promise.promise();
    MySQLPool pool = database.getPool();
    pool.preparedQuery("insert into users(username, password, name) values (?,?,?)")
        .execute(
            info,
            rowSetRs -> {
              if (rowSetRs.succeeded()) {
                RowSet<Row> rs = rowSetRs.result();
                result.complete(rs.property(MySQLClient.LAST_INSERTED_ID).intValue());
              } else {
                result.fail(rowSetRs.cause());
              }
            });
    return result.future();
  }

  private Future<String> genToken(int userId) {
    Promise<String> tokenPromise = Promise.promise();
    tokenPromise.complete(auth.generateToken(new JsonObject().put("userId", userId)));
    return tokenPromise.future();
  }
}
