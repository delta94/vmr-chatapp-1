package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDBService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.util.ControllerUtil;
import com.anhvan.vmr.websocket.WebSocketService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Builder
@Log4j2
public class UserController extends BaseController {
  private UserDBService userDBService;
  private UserCacheService userCacheService;
  private WebSocketService webSocketService;

  @Override
  public Future<BaseResponse> handleGet(BaseRequest request) {
    Promise<BaseResponse> userListPromise = Promise.promise();

    // Get users from cache
    Future<List<User>> cachedList = userCacheService.getUserList();

    cachedList.onComplete(
        listAsyncResult -> {
          JsonObject data = new JsonObject();

          if (listAsyncResult.succeeded()) {
            // Cache hit
            log.debug("Get user list, cache hit");

            List<User> userList = listAsyncResult.result();
            setOnline(userList);
            data.put("userList", userList);

            // Response to user
            userListPromise.complete(
                BaseResponse.builder()
                    .statusCode(200)
                    .data(data)
                    .message("Get user list successfully")
                    .build());
          } else {
            // Cache miss
            log.trace("Cache miss");

            // Load user form database
            Future<List<User>> userListFuture = userDBService.getListUser();
            userListFuture.onSuccess(
                users -> {
                  setOnline(users);
                  data.put("userList", users);
                  userCacheService.setUserList(users);

                  // Response to user
                  userListPromise.complete(
                      BaseResponse.builder()
                          .statusCode(200)
                          .data(data)
                          .message("Get user list successfully")
                          .build());
                });
          }
        });

    return userListPromise.future();
  }

  public void setOnline(List<User> userList) {
    Set<Integer> onlineSet = webSocketService.getOnlineIds();
    userList.forEach(user -> user.setOnline(onlineSet.contains(user.getId())));
  }
}
