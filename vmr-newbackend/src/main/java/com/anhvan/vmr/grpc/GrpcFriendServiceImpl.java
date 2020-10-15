package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.entity.Friend;
import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.Common.Error;
import com.anhvan.vmr.proto.Common.ErrorCode;
import com.anhvan.vmr.proto.Common.Empty;
import com.anhvan.vmr.proto.Friend.*;
import com.anhvan.vmr.proto.FriendServiceGrpc.*;
import com.anhvan.vmr.service.FriendService;
import com.anhvan.vmr.service.UserService;
import com.anhvan.vmr.util.GrpcUtil;
import com.anhvan.vmr.websocket.WebSocketService;
import io.grpc.stub.StreamObserver;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class GrpcFriendServiceImpl extends FriendServiceImplBase {
  private UserDatabaseService userDbService;
  private FriendDatabaseService friendDbService;
  private WebSocketService webSocketService;
  private FriendService friendService;
  private UserService userService;

  @Override
  public void getFriendList(Empty request, StreamObserver<FriendListResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    log.info("Handle getFriendList, userId={}", userId);

    friendDbService
        .getFriendList(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                FriendListResponse.Builder response = FriendListResponse.newBuilder();
                List<Friend> grpcUserResponseList = ar.result();
                for (Friend user : grpcUserResponseList) {
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
                log.error("Error when get friend list, userId={}", userId, ar.cause());
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
  public void setFriendStatus(
      SetFriendStatusRequest request, StreamObserver<SetFriendStatusResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());
    long friendId = request.getFriendId();

    SetFriendStatusRequest.Type type = request.getType();

    Future<Void> setFriendStatusFuture = null;

    switch (type) {
      case REMOVE_FRIEND:
        log.info("Hanlde remove friend request: userId={}, friendId={}", userId, friendId);
        setFriendStatusFuture = friendService.removeFriend(userId, friendId);
        break;
      case ADD_FRIEND:
        log.info("Hanlde add friend request: userId={}, friendId={}", userId, friendId);
        setFriendStatusFuture = friendDbService.addFriend(userId, friendId);
        break;
      case ACCEPT_FRIEND:
        log.info("Hanlde accept friend request: userId={}, friendId={}", userId, friendId);
        setFriendStatusFuture = friendService.acceptFriend(friendId, userId);
        break;
      case REJECT_FRIEND:
        log.info("Hanlde reject friend request: userId={}, friendId={}", userId, friendId);
        setFriendStatusFuture = friendDbService.rejectFriend(friendId, userId);
        break;
    }

    SetFriendStatusResponse.Builder responseBuilder = SetFriendStatusResponse.newBuilder();

    if (setFriendStatusFuture == null) {
      responseObserver.onNext(
          responseBuilder
              .setError(Error.newBuilder().setCode(ErrorCode.INTERNAL_SERVER_ERROR).build())
              .build());
      responseObserver.onCompleted();
      return;
    }

    setFriendStatusFuture.onComplete(
        ar -> {
          if (ar.succeeded()) {
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            if (type == SetFriendStatusRequest.Type.ACCEPT_FRIEND) {
              webSocketService.sendTo(
                  friendId,
                  WebSocketMessage.builder()
                      .data(new JsonObject().put("userId", userId))
                      .type(WebSocketMessage.Type.ACCEPT.name())
                      .build());
            }
            if (type == SetFriendStatusRequest.Type.REMOVE_FRIEND) {
              webSocketService.sendTo(
                  friendId,
                  WebSocketMessage.builder()
                      .data(new JsonObject().put("userId", userId))
                      .type(WebSocketMessage.Type.REMOVE_FRIEND.name())
                      .build());
            }
          } else {
            log.error(
                "Error when set friend status: user_id={}, friend_id={}",
                userId,
                friendId,
                ar.cause());
            Error error =
                Error.newBuilder()
                    .setCode(ErrorCode.INTERNAL_SERVER_ERROR)
                    .setMessage("Error when set friend status")
                    .build();
            responseObserver.onNext(responseBuilder.setError(error).build());
            responseObserver.onCompleted();
          }
        });
  }

  @Override
  public void getChatFriendList(
      Empty request, StreamObserver<FriendListResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    FriendListResponse.Builder responseBuilder = FriendListResponse.newBuilder();

    friendService
        .getChatFriendList(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                for (Friend usr : ar.result()) {
                  FriendInfo.Builder friendInfoBuidler =
                      FriendInfo.newBuilder()
                          .setId(usr.getId())
                          .setName(usr.getName())
                          .setUsername(usr.getUsername())
                          .setOnline(webSocketService.checkOnline(usr.getId()))
                          .setNumUnreadMessage(usr.getNumUnreadMessage());

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

    Future<List<Friend>> userListFuture =
        userDbService.queryListUserWithFriendStatus(queryString, userId);

    userListFuture.onSuccess(
        userList -> {
          UserListResponse.Builder response = UserListResponse.newBuilder();

          // Convert user object to response
          for (Friend user : userList) {
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
  public void clearUnreadMessage(
      ClearUnreadMessageRequest request,
      StreamObserver<ClearUnreadMessageResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    friendService
        .clearUnreadMessage(userId, request.getFriendId())
        .onComplete(
            ar -> {
              ClearUnreadMessageResponse.Builder builder = ClearUnreadMessageResponse.newBuilder();
              if (ar.failed()) {
                log.error(
                    "Error when clear unread message: user={}, friend={}",
                    userId,
                    request.getFriendId(),
                    ar.cause());
                responseObserver.onNext(
                    builder
                        .setError(
                            Error.newBuilder().setCode(ErrorCode.INTERNAL_SERVER_ERROR).build())
                        .build());
              } else {
                responseObserver.onNext(builder.build());
              }
              responseObserver.onCompleted();
            });
  }

  @Override
  public void getUserInfo(
      GetUserInfoRequest request, StreamObserver<GetUserInfoResponse> responseObserver) {
    long userId = request.getUserId();

    userService
        .getUserById(userId)
        .onComplete(
            ar -> {
              GetUserInfoResponse.Builder responseBuilder = GetUserInfoResponse.newBuilder();
              if (ar.succeeded()) {
                User user = ar.result();
                responseBuilder
                    .setUser(
                        UserResponse.newBuilder()
                            .setId(userId)
                            .setName(user.getName())
                            .setUsername(user.getUsername())
                            .build())
                    .build();
              } else {
                log.error("Error when get user info: userId={}", userId, ar.cause());
                responseBuilder.setError(
                    Error.newBuilder().setCode(ErrorCode.INTERNAL_SERVER_ERROR).build());
              }
              responseObserver.onNext(responseBuilder.build());
              responseObserver.onCompleted();
            });
  }
}
