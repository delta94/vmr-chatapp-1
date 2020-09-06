package com.anhvan.vmr.cache;

import io.vertx.core.Future;

public interface TokenCacheService {
  void addToBlackList(String token);

  Future<Boolean> checkExistInBacklist(String token);
}
