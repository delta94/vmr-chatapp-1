package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.GrpcUserResponse;
import io.vertx.core.Future;

import java.util.List;

public interface FriendDatabaseService {
  Future<Long> addFriend(long userId, long friendId);

  Future<List<GrpcUserResponse>> getFriendList(long userId);

  Future<List<GrpcUserResponse>> getChatFriendList(long userId);

  Future<String> acceptFriend(long invitorId, long userId);

  Future<String> rejectFriend(long invitorId, long userId);

  Future<Void> clearUnreadMessage(long userId, long friendId);
}
