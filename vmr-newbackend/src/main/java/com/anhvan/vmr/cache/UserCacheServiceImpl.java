package com.anhvan.vmr.cache;

import com.anhvan.vmr.config.CacheConfig;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class UserCacheServiceImpl implements UserCacheService {
  private RedissonClient redis;
  private AsyncWorkerUtil workerUtil;
  private CacheConfig cacheConfig;

  @Inject
  public UserCacheServiceImpl(
      RedisCache redisCache, AsyncWorkerUtil workerUtil, CacheConfig cacheConfig) {
    redis = redisCache.getRedissonClient();
    this.workerUtil = workerUtil;
    this.cacheConfig = cacheConfig;
  }

  private String getUserKey(int userId) {
    String keyPattern = "vmr:user:%d:info";
    return String.format(keyPattern, userId);
  }

  @Override
  public void setUserCache(User user) {
    String key = getUserKey(user.getId());
    RMap<String, String> userInfo = redis.getMap(key);
    workerUtil.execute(
        () -> {
          userInfo.put("name", user.getName());
          userInfo.put("username", user.getUsername());
          userInfo.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
        });
  }

  @Override
  public Future<User> getUserCache(int userId) {
    Promise<User> userPromise = Promise.promise();

    RMap<String, String> userInfo = redis.getMap(getUserKey(userId));
    workerUtil.execute(
        () -> {
          if (!userInfo.isExists()) {
            userPromise.fail("User not exist in cache");
            return;
          }
          String name = userInfo.get("name");
          String username = userInfo.get("username");
          userInfo.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
          User user = User.builder().id(userId).username(username).name(name).build();
          userPromise.complete(user);
        });

    return userPromise.future();
  }

  @Override
  public void setUserList(List<User> userList) {
    workerUtil.execute(
        () -> {
          RQueue<User> userSet = redis.getQueue("vmr:users");
          userSet.clear();
          userSet.addAll(userList);
          userSet.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
        });
  }

  @Override
  public void addUserList(User user) {
    workerUtil.execute(
        () -> {
          RQueue<User> userSet = redis.getQueue("vmr:users");
          if (userSet.isExists()) {
            userSet.add(user);
            userSet.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
          }
        });
  }

  @Override
  public Future<List<User>> getUserList() {
    Promise<List<User>> listUserPromise = Promise.promise();

    workerUtil.execute(
        () -> {
          RQueue<User> userQueue = redis.getQueue("vmr:users");
          if (!userQueue.isExists()) {
            listUserPromise.fail("List user not exist in cache");
            return;
          }
          listUserPromise.complete(userQueue.readAll());
        });

    return listUserPromise.future();
  }
}
