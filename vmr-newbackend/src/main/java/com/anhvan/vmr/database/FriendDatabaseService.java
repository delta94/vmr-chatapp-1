package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.Friend;
import io.vertx.core.Future;

import java.util.List;

public interface FriendDatabaseService {
  Future<Void> addFriend(long userId, long friendId);

  Future<List<Friend>> getFriendList(long userId);

  Future<List<Friend>> getChatFriendList(long userId);

  Future<Void> acceptFriend(long userId, long friendId);

  Future<Void> rejectFriend(long userId, long friendId);

  Future<Void> removeFriend(long userId, long friendId);

  Future<Void> clearUnreadMessage(long userId, long friendId);
}
