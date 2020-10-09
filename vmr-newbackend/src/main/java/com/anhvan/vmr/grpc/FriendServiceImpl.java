package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.entity.GrpcUserResponse;
import com.anhvan.vmr.proto.Common.Error;
import com.anhvan.vmr.proto.Common.ErrorCode;
import com.anhvan.vmr.proto.Common.Empty;
import com.anhvan.vmr.proto.Friend.*;
import com.anhvan.vmr.proto.FriendServiceGrpc.*;
import com.anhvan.vmr.util.GrpcUtil;
import com.anhvan.vmr.websocket.WebSocketService;
import io.grpc.stub.StreamObserver;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class FriendServiceImpl extends FriendServiceImplBase {
  private UserDatabaseService userDbService;
  private FriendDatabaseService friendDbService;
  private WebSocketService webSocketService;

  @Override
  public void addFriend(
      AddFriendRequest request, StreamObserver<AddFriendResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());
    long friendId = request.getUserId();

    // Check if friendId is valid
    if (userId == friendId) {
      Error error =
          Error.newBuilder()
              .setCode(ErrorCode.FAILUE)
              .setMessage("You cannot add yourself as friend")
              .build();
      responseObserver.onNext(AddFriendResponse.newBuilder().setError(error).build());
      responseObserver.onCompleted();
      return;
    }

    friendDbService
        .addFriend(userId, friendId)
        .onComplete(
            ar -> {
              AddFriendResponse.Builder responseBuilder = AddFriendResponse.newBuilder();
              if (ar.succeeded()) {
                // Add friend succeeded
                responseObserver.onNext(responseBuilder.build());
              } else {
                // Add friend failed
                log.error(
                    "Fail to add friend userId: {}, friendId: {}", userId, friendId, ar.cause());
                Error error =
                    Error.newBuilder()
                        .setCode(ErrorCode.FAILUE)
                        .setMessage("Fail to add friend")
                        .build();
                responseObserver.onNext(responseBuilder.setError(error).build());
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
                List<GrpcUserResponse> grpcUserResponseList = ar.result();
                for (GrpcUserResponse user : grpcUserResponseList) {
                  FriendInfo info =
                      FriendInfo.newBuilder()
                          .setUsername(user.getUsername())
                          .setName(user.getName())
                          .setId(user.getId())
                          .setFriendStatus(GrpcUtil.string2FriendStatus(user.getFriendStatus()))
                          .build();
                  response.addFriendInfo(info);
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

  @Override
  public void acceptFriend(
      AcceptFriendRequest request, StreamObserver<AcceptFriendResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    AcceptFriendResponse.Builder responseBuilder = AcceptFriendResponse.newBuilder();

    friendDbService
        .acceptFriend(request.getFriendId(), userId)
        .onComplete(
            ar -> {
              if (ar.failed()) {
                log.debug(
                    "Fail to accept friend request userId:{}, friendId:{}",
                    userId,
                    request.getFriendId(),
                    ar.cause());
                responseBuilder.setError(
                    Error.newBuilder().setMessage("Accept friend failed").build());
              }
              responseObserver.onNext(responseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  @Override
  public void rejectFriend(
      RejectFriendRequest request, StreamObserver<RejectFriendResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    RejectFriendResponse.Builder responseBuilder = RejectFriendResponse.newBuilder();

    friendDbService
        .rejectFriend(request.getFriendId(), userId)
        .onComplete(
            ar -> {
              if (ar.failed()) {
                log.debug(
                    "Fail to reject friend request userId:{}, friendId:{}",
                    userId,
                    request.getFriendId(),
                    ar.cause());
                responseBuilder.setError(
                    Error.newBuilder().setMessage("Remove friend failed").build());
              }
              responseObserver.onNext(responseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  @Override
  public void getChatFriendList(
      Empty request, StreamObserver<FriendListResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    FriendListResponse.Builder responseBuilder = FriendListResponse.newBuilder();

    friendDbService
        .getChatFriendList(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                for (GrpcUserResponse usr : ar.result()) {
                  FriendInfo.Builder friendInfoBuidler =
                      FriendInfo.newBuilder()
                          .setId(usr.getId())
                          .setName(usr.getName())
                          .setUsername(usr.getUsername())
                          .setOnline(webSocketService.checkOnline(usr.getId()));

                  if (usr.getLastMessage() != null) {
                    friendInfoBuidler
                        .setLastMessage(usr.getLastMessage())
                        .setLastMessageTimestamp(usr.getLastMessageTimestamp())
                        .setLastMessageSender(usr.getLastMessageSenderId())
                        .setLastMessageType(usr.getLastMessageType());
                  }

                  responseBuilder.addFriendInfo(friendInfoBuidler.build());
                }
              } else {
                log.error("Error when get friend of users {}", userId, ar.cause());
                responseBuilder.setError(
                    Error.newBuilder().setMessage("Error when get friend list").build());
              }
              responseObserver.onNext(responseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  @Override
  public void queryUser(
      UserListRequest request, StreamObserver<UserListResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    String queryString = request.getQueryString();

    Future<List<GrpcUserResponse>> userListFuture =
        userDbService.queryListUserWithFriend(queryString, userId);

    userListFuture.onSuccess(
        userList -> {
          UserListResponse.Builder response = UserListResponse.newBuilder();

          // Convert user object to response
          for (GrpcUserResponse user : userList) {
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
}
