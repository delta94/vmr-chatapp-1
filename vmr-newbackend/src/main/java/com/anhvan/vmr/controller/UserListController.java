package com.anhvan.vmr.controller;

import com.anhvan.vmr.cache.UserCacheService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.entity.BaseRequest;
import com.anhvan.vmr.entity.BaseResponse;
import com.anhvan.vmr.entity.UserResponse;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.websocket.WebSocketService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Log4j2
public class UserListController extends BaseController {
  private UserDatabaseService userDBService;
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
            List<UserResponse> userResponseList = getUserResponseList(listAsyncResult.result());
            data.put("userList", userResponseList);

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
                userList -> {
                  List<UserResponse> userResponseList = getUserResponseList(userList);

                  data.put("userList", userResponseList);

                  // Update cache
                  userCacheService.setUserList(userList);

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

  public List<UserResponse> getUserResponseList(List<User> userList) {
    Set<Integer> onlineSet = webSocketService.getOnlineIds();
    return userList.stream()
        .map(
            user -> {
              UserResponse userResponse = UserResponse.fromUser(user);
              userResponse.setOnline(onlineSet.contains(user.getId()));
              return userResponse;
            })
        .collect(Collectors.toList());
  }
}
