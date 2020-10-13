package com.anhvan.vmr.service;

import com.anhvan.vmr.entity.Friend;
import io.vertx.core.Future;

import java.util.List;

public interface FriendService {
  Future<List<Friend>> getChatFriendList(long userId);

  Future<Void> clearUnreadMessage(long userId, long friendId);

  Future<Void> acceptFriend(long userId, long friendId);

  Future<Void> removeFriend(long userId, long friendId);
}
