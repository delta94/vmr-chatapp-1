package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.entity.UserWithStatus;
import com.anhvan.vmr.proto.ErrorOuterClass.Error;
import com.anhvan.vmr.proto.ErrorOuterClass.*;
import com.anhvan.vmr.proto.Friend.*;
import com.anhvan.vmr.proto.FriendServiceGrpc.*;
import com.anhvan.vmr.proto.EmptyOuterClass.Empty;
import com.anhvan.vmr.util.GrpcUtil;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class FriendServiceImpl extends FriendServiceImplBase {
  private FriendDatabaseService friendDbService;

  @Override
  public void addFriend(
      AddFriendRequest request, StreamObserver<AddFriendResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());
    long friendId = request.getUserId();

    if (userId == friendId) {
      responseObserver.onNext(
          AddFriendResponse.newBuilder()
              .setError(
                  Error.newBuilder()
                      .setCode(ErrorCode.FAILUE)
                      .setMessage("You cannot add yourself as friend")
                      .build())
              .build());
      responseObserver.onCompleted();
      return;
    }

    friendDbService
        .addFriend(userId, friendId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                responseObserver.onNext(AddFriendResponse.newBuilder().build());
              } else {
                log.error(
                    "Fail to add friend userId: {}, friendId: {}", userId, friendId, ar.cause());
                responseObserver.onNext(
                    AddFriendResponse.newBuilder()
                        .setError(
                            Error.newBuilder()
                                .setCode(ErrorCode.FAILUE)
                                .setMessage("Fail to add friend")
                                .build())
                        .build());
              }
              responseObserver.onCompleted();
            });
  }

  @Override
  public void getFriendList(Empty request, StreamObserver<FriendListResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    friendDbService
        .getFriendList(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                FriendListResponse.Builder response = FriendListResponse.newBuilder();
                List<UserWithStatus> userWithStatusList = ar.result();
                for (UserWithStatus user : userWithStatusList) {
                  response.addFriendInfo(
                      FriendInfo.newBuilder()
                          .setUsername(user.getUsername())
                          .setName(user.getName())
                          .setId(user.getId())
                          .setFriendStatus(GrpcUtil.string2FriendStatus(user.getFriendStatus()))
                          .build());
                }
                responseObserver.onNext(response.build());
              } else {
                responseObserver.onNext(
                    FriendListResponse.newBuilder()
                        .setError(
                            Error.newBuilder()
                                .setCode(ErrorCode.FAILUE)
                                .setMessage("Fail to load friend list")
                                .build())
                        .build());
              }
              responseObserver.onCompleted();
            });
  }
}
