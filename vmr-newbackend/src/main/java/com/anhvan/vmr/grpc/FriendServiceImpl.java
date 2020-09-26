package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.FriendDatabaseService;
import com.anhvan.vmr.proto.FriendOuterClass;
import com.anhvan.vmr.proto.FriendServiceGrpc.*;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class FriendServiceImpl extends FriendServiceImplBase {
  FriendDatabaseService friendDbService;

  @Override
  public void addFriend(
      FriendOuterClass.AddFriendRequest request,
      StreamObserver<FriendOuterClass.AddFriendResponse> responseObserver) {
    super.addFriend(request, responseObserver);
  }
}
