package com.anhvan.vmr.cache;

import java.util.List;

public interface FriendCacheService {
  void cacheFriendList(long userId, List<Integer> friendIdList);
}
