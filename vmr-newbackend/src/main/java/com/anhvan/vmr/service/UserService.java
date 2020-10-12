package com.anhvan.vmr.service;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

public interface UserService {
  Future<User> getUserById(long id);
}
