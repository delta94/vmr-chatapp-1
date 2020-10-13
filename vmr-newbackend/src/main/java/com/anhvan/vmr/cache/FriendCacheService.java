package com.anhvan.vmr.cache;

import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.model.Message;
import io.vertx.core.Future;

import java.util.List;

public interface FriendCacheService {
  Future<Void> cacheFriendList(long userId, List<Friend> friendList);

  Future<Void> cacheFriend(long userId, Friend friend);

  Future<Void> removeFriend(long userId, long friendId);

  Future<List<Friend>> getFriendList(long userId);

  Future<Void> clearFriendCache(long userId);

  Future<Void> updateLastMessage(long userId, long friendId, Message message);

  Future<Void> clearUnreadMessage(long userId, long friendId);
}
