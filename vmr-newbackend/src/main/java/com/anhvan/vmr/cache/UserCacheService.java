package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface UserCacheService {
  String getUserKey(int userId);

  void setUserCache(User user);

  Future<User> getUserCache(int userId);

  void setUserList(List<User> userList);

  void addUserList(User user);

  Future<List<User>> getUserList();
}
