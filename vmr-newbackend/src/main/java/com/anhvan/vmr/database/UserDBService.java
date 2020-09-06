package com.anhvan.vmr.database;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface UserDBService {
  Future<Integer> addUser(User user);

  Future<User> getUserByUsername(String username);

  Future<User> getUserById(int id);

  Future<List<User>> getListUser();
}
