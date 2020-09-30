package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.Common;
import com.anhvan.vmr.proto.Wallet;
import com.anhvan.vmr.proto.WalletServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class WalletServiceImpl extends WalletServiceGrpc.WalletServiceImplBase {
  private UserDatabaseService userDbService;

  @Override
  public void getBalance(
      Common.Empty request, StreamObserver<Wallet.BalanceResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());
    Wallet.BalanceResponse.Builder responseBuilder = Wallet.BalanceResponse.newBuilder();

    userDbService
        .getUserById(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                // Get info success
                User user = ar.result();
                Wallet.BalanceResponse.Data data =
                    Wallet.BalanceResponse.Data.newBuilder()
                        .setBalance(user.getBalance())
                        .setLastUpdated(user.getLastUpdated())
                        .build();
                responseBuilder.setData(data);
              } else {
                // Get info failue
                Common.Error error =
                    Common.Error.newBuilder()
                        .setMessage("Cannot get balance " + "information")
                        .build();
                responseBuilder.setError(error);
              }
              responseObserver.onNext(responseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  @Override
  public void getHistory(
      Common.Empty request, StreamObserver<Wallet.HistoryResponse> responseObserver) {

  }
}
