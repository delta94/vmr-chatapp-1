package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

public interface UserCacheService {
  Future<Void> cacheUser(User user);

  Future<User> getUser(long userId);
}
