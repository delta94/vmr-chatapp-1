package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.UserWithStatus;
import io.vertx.core.Future;

import java.util.List;

public interface FriendDatabaseService {
  Future<Long> addFriend(long userId, long friendId);

  Future<List<UserWithStatus>> getFriendList(long userId);

  Future<List<UserWithStatus>> getChatFriendList(long userId);

  Future<String> acceptFriend(long invitorId, long userId);

  Future<String> rejectFriend(long invitorId, long userId);
}
