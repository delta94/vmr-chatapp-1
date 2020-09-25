package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.User.UserListRequest;
import com.anhvan.vmr.proto.User.UserListResponse;
import com.anhvan.vmr.proto.UserServiceGrpc.UserServiceImplBase;
import io.grpc.stub.StreamObserver;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class UserServiceImpl extends UserServiceImplBase {
  private UserDatabaseService userDbService;

  @Override
  public void queryUser(
      UserListRequest request, StreamObserver<UserListResponse> responseObserver) {
    String queryString = request.getQueryString();

    Future<List<User>> userListFuture = userDbService.queryListUser(queryString);

    userListFuture.onSuccess(
        userList -> {
          UserListResponse.Builder response = UserListResponse.newBuilder();
          for (User user : userList) {
            response.addUserList(
                UserListResponse.UserResponse.newBuilder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setName(user.getName())
                    .build());
          }
          responseObserver.onNext(response.build());
          responseObserver.onCompleted();
        });

    userListFuture.onFailure(
        event -> responseObserver.onError(new Exception("Internal server error")));
  }
}
