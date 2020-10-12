package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface UserCacheService {
  void setUserCache(User user);

  Future<User> getUserCache(long userId);
}
