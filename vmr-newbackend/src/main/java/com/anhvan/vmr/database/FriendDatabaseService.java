package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.UserWithStatus;
import com.anhvan.vmr.model.User;
import io.vertx.core.Future;

import java.util.List;

public interface FriendDatabaseService {
  Future<Long> addFriend(long userId, long friendId);

  Future<List<UserWithStatus>> getFriendList(long userId);

  Future<List<User>> getFriendInvitation(long userId);

  Future<String> acceptFriend(long invitorId, long userId);
}
