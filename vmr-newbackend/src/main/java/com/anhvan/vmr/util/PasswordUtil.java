package com.anhvan.vmr.util;

import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.model.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jodd.crypt.BCrypt;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class PasswordUtil {
  private AsyncWorkerUtil workerUtil;
  private UserDatabaseService userDatabaseService;

  @Inject
  public PasswordUtil(AsyncWorkerUtil asyncWorkerUtil, UserDatabaseService userDatabaseService) {
    this.workerUtil = asyncWorkerUtil;
    this.userDatabaseService = userDatabaseService;
  }

  public Future<Boolean> checkPassword(long userId, String password) {
    Promise<Boolean> checkPasswordPromise = Promise.promise();

    userDatabaseService
        .getUserById(userId)
        .onComplete(
            ar -> {
              if (ar.failed()) {
                checkPasswordPromise.complete(false);
                log.debug("Fail to get user", ar.cause());
                return;
              }
              workerUtil.execute(
                  () ->
                      checkPasswordPromise.complete(
                          BCrypt.checkpw(password, ar.result().getPassword())));
            });

    return checkPasswordPromise.future();
  }
}
