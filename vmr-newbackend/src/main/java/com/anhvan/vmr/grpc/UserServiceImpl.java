package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.entity.UserWithStatus;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.EmptyOuterClass;
import com.anhvan.vmr.proto.User.UserListRequest;
import com.anhvan.vmr.proto.User.UserListResponse;
import com.anhvan.vmr.proto.User.UserResponse;
import com.anhvan.vmr.proto.UserServiceGrpc.UserServiceImplBase;
import com.anhvan.vmr.util.GrpcUtil;
import io.grpc.stub.StreamObserver;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class UserServiceImpl extends UserServiceImplBase {
  private UserDatabaseService userDbService;
  private FriendDatabaseService friendDatabaseService;

  @Override
  public void queryUser(
      UserListRequest request, StreamObserver<UserListResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    String queryString = request.getQueryString();

    Future<List<UserWithStatus>> userListFuture =
        userDbService.queryListUserWithFriend(queryString, userId);

    userListFuture.onSuccess(
        userList -> {
          UserListResponse.Builder response = UserListResponse.newBuilder();

          // Convert user object to response
          for (UserWithStatus user : userList) {
            response.addUser(
                UserResponse.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setName(user.getName())
                    .setFriendStatus(GrpcUtil.string2FriendStatus(user.getFriendStatus()))
                    .build());
          }

          // Send result to client
          responseObserver.onNext(response.build());
          responseObserver.onCompleted();
        });

    userListFuture.onFailure(
        event -> responseObserver.onError(new Exception("Internal server error")));
  }

  @Override
  public void getInfo(
      EmptyOuterClass.Empty request, StreamObserver<UserResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    userDbService
        .getUserById(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                User user = ar.result();
                UserResponse response =
                    UserResponse.newBuilder()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setName(user.getName())
                        .build();
                responseObserver.onNext(response);
              } else {
                log.error("Error when get user info");
              }
              responseObserver.onCompleted();
            });
  }
}
