package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.GrpcUserResponse;
import io.vertx.core.Future;

import java.util.List;

public interface FriendDatabaseService {
  Future<Void> addFriend(long userId, long friendId);

  Future<List<GrpcUserResponse>> getFriendList(long userId);

  Future<List<GrpcUserResponse>> getChatFriendList(long userId);

  Future<Void> acceptFriend(long invitorId, long userId);

  Future<Void> rejectFriend(long invitorId, long userId);

  Future<Void> removeFriend(long userId, long friendId);

  Future<Void> clearUnreadMessage(long userId, long friendId);
}
