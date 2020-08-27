package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class UserCacheService {
  private RedissonClient client;

  @Inject
  public UserCacheService(RedisCache redisCache) {
    client = redisCache.getRedissonClient();
  }

  public String getKey(int userId) {
    String keyPattern = "vmr:user:%d:info";
    return String.format(keyPattern, userId);
  }

  public void setUserCache(User user) {
    String key = getKey(user.getId());
    RMap<String, String> userInfo = client.getMap(key);
    userInfo.putAsync("name", user.getName());
    userInfo.putAsync("username", user.getUsername());
    userInfo.expire(10, TimeUnit.MINUTES);
  }

  public Future<User> getUserCache(int userId) {
    Promise<User> userPromise = Promise.promise();
    RMap<String, String> userInfo = client.getMap(getKey(userId));
    userInfo
        .getAllAsync(new HashSet<>(Arrays.asList("username", "name")))
        .whenComplete(
            (infoMap, throwable) -> {
              if (throwable != null) {
                userPromise.fail(throwable);
                return;
              }
              User user =
                  User.builder()
                      .username(infoMap.get("username"))
                      .name(userInfo.getName())
                      .id(userId)
                      .build();
              userPromise.complete(user);
            });
    userInfo.expire(10, TimeUnit.MINUTES);
    return userPromise.future();
  }
}
