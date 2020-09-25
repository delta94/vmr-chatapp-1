package com.anhvan.vmr.database;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface UserDatabaseService {
  Future<Integer> addUser(User user);

  Future<User> getUserByUsername(String username);

  Future<User> getUserById(int id);

  Future<List<User>> queryListUser(String query);

  Future<List<User>> getListUser();
}
