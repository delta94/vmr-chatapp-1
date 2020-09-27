package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.UserWithStatus;
import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface UserDatabaseService {
  Future<Long> addUser(User user);

  Future<User> getUserByUsername(String username);

  Future<User> getUserById(long id);

  Future<List<User>> queryListUser(String query);

  Future<List<User>> getListUser();

  Future<List<UserWithStatus>> queryListUserWithFriend(String query, long userId);
}
