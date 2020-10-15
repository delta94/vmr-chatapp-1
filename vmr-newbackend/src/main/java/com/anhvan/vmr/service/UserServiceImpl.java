package com.anhvan.vmr.service;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.model.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Log4j2
public class UserServiceImpl implements UserService {
  private final UserCacheService userCacheService;
  private final UserDatabaseService userDatabaseService;

  @Inject
  public UserServiceImpl(
      UserCacheService userCacheService, UserDatabaseService userDatabaseService) {
    this.userCacheService = userCacheService;
    this.userDatabaseService = userDatabaseService;
  }

  @Override
  public Future<User> getUserById(long userId) {
    Promise<User> userPromise = Promise.promise();

    Future<User> userCacheFuture = userCacheService.getUserCache(userId);

    userCacheFuture.onComplete(
        cacheAr -> {
          if (cacheAr.succeeded()) {
            log.debug("Get user with userId={} - cache hit", userId);
            userPromise.complete(cacheAr.result());
            return;
          }

          log.debug("Get user with userId={} - cache miss", userId);

          userDatabaseService
              .getUserById(userId)
              .onComplete(
                  dbAr -> {
                    if (dbAr.succeeded()) {
                      User result = dbAr.result();
                      userPromise.complete(result);
                      userCacheService.setUserCache(result);
                      return;
                    }

                    log.error(
                        "Error when get user from database with userId={}", userId, dbAr.cause());
                    userPromise.fail("Fail when get user info");
                  });
        });

    return userPromise.future();
  }
}
