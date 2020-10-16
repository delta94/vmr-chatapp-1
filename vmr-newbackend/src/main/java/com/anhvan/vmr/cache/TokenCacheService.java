package com.anhvan.vmr.cache;

import io.vertx.core.Future;

public interface TokenCacheService {
  Future<Void> addToBlackList(String token);

  Future<Boolean> checkExistInBacklist(String token);
}
