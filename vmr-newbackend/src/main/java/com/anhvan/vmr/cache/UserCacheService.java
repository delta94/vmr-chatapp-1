package com.anhvan.vmr.cache;

import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.AsyncWorkerUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public class UserCacheService {
  private RedissonClient redis;
  private AsyncWorkerUtil workerUtil;

  @Inject
  public UserCacheService(RedisCache redisCache, AsyncWorkerUtil workerUtil) {
    redis = redisCache.getRedissonClient();
    this.workerUtil = workerUtil;
  }

  public String getUserKey(int userId) {
    String keyPattern = "vmr:user:%d:info";
    return String.format(keyPattern, userId);
  }

  public void setUserCache(User user) {
    String key = getUserKey(user.getId());
    RMap<String, String> userInfo = redis.getMap(key);
    userInfo.putAsync("name", user.getName());
    userInfo.putAsync("username", user.getUsername());
    userInfo.expireAsync(10, TimeUnit.MINUTES);
  }

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
          userInfo.expire(10, TimeUnit.MINUTES);
          User user = User.builder().id(userId).username(username).name(name).build();
          userPromise.complete(user);
        });
    return userPromise.future();
  }

  public void setUserList(List<User> userList) {
    workerUtil.execute(
        () -> {
          RQueue<User> userQueue = redis.getQueue("vmr:users");
          userQueue.clear();
          userQueue.addAll(userList);
          userQueue.expire(1, TimeUnit.MINUTES);
        });
  }

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
