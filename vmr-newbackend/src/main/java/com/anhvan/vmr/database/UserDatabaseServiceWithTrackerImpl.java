package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
import com.anhvan.vmr.util.AsyncUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.SqlClient;

public class UserDatabaseServiceWithTrackerImpl extends UserDatabaseServiceImpl {
  private TimeTracker getUserByIdTracker;

  public UserDatabaseServiceWithTrackerImpl(
      SqlClient sqlClient, AsyncWorkerService workerUtil, TrackerService trackerService) {
    super(sqlClient, workerUtil);
    getUserByIdTracker =
        trackerService.getTimeTracker("database_query_time", "method", "getUserById");
  }

  @Override
  public Future<User> getUserById(long id) {
    Promise<User> promise = Promise.promise();
    TimeTracker.Tracker tracker = getUserByIdTracker.start();

    super.getUserById(id)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }
}
