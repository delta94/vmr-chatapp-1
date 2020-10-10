package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.GrpcUserResponse;
import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface UserDatabaseService {
  /**
   * @param user user object to add to database
   * @return new user id
   */
  Future<Long> addUser(User user);

  /**
   * @param username username of the user
   * @return user object if exist
   */
  Future<User> getUserByUsername(String username);

  /**
   * @param id unique id of the user
   * @return user object if exist
   */
  Future<User> getUserById(long id);

  /** @return list of all user in the system */
  Future<List<User>> getListUser();

  /**
   * @param query query string username | name
   * @param userId userId
   * @return list of user in the system with friendstatus status
   */
  Future<List<GrpcUserResponse>> queryListUserWithFriendStatus(String query, long userId);
}
