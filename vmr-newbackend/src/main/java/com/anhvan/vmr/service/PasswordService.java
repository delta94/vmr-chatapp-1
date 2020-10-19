package com.anhvan.vmr.service;

import com.anhvan.vmr.database.UserDatabaseService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import jodd.crypt.BCrypt;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class PasswordService {
  private AsyncWorkerService workerUtil;
  private UserDatabaseService userDatabaseService;

  @Inject
  public PasswordService(
      AsyncWorkerService asyncWorkerService, UserDatabaseService userDatabaseService) {
    this.workerUtil = asyncWorkerService;
    this.userDatabaseService = userDatabaseService;
  }

  public Future<Void> checkPassword(long userId, String password) {
    Promise<Void> checkPasswordPromise = Promise.promise();

    userDatabaseService
        .getUserById(userId)
        .onComplete(
            ar -> {
              if (ar.failed()) {
                checkPasswordPromise.fail(new RuntimeException("Cannot access database"));
                log.debug("Fail to get user", ar.cause());
                return;
              }
              workerUtil.execute(
                  () -> {
                    if (BCrypt.checkpw(password, ar.result().getPassword())) {
                      checkPasswordPromise.complete();
                    } else {
                      checkPasswordPromise.fail("Password is invalid");
                    }
                  });
            });

    return checkPasswordPromise.future();
  }
}
