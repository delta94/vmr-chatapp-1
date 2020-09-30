package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.database.WalletDatabaseService;
import com.anhvan.vmr.entity.History;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.Common;
import com.anhvan.vmr.proto.Wallet.*;
import com.anhvan.vmr.proto.WalletServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class WalletServiceImpl extends WalletServiceGrpc.WalletServiceImplBase {
  private UserDatabaseService userDbService;
  private WalletDatabaseService walletDatabaseService;

  @Override
  public void getBalance(Common.Empty request, StreamObserver<BalanceResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    BalanceResponse.Builder responseBuilder = BalanceResponse.newBuilder();

    userDbService
        .getUserById(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                // Get info success
                User user = ar.result();
                BalanceResponse.Data data =
                    BalanceResponse.Data.newBuilder()
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
  public void getHistory(Common.Empty request, StreamObserver<HistoryResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    HistoryResponse.Builder historyResponseBuilder = HistoryResponse.newBuilder();

    walletDatabaseService
        .getHistory(userId)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                List<History> historyList = ar.result();
                for (History history : historyList) {
                  HistoryResponse.Type type = HistoryResponse.Type.TRANSFER;
                  if (history.getType() == History.Type.RECEIVE) {
                    type = HistoryResponse.Type.RECEIVE;
                  }
                  historyResponseBuilder.addItem(
                      HistoryResponse.Item.newBuilder()
                          .setId(history.getId())
                          .setSender(history.getSender())
                          .setReceiver(history.getReceiver())
                          .setAmount(history.getAmount())
                          .setBalance(history.getBalance())
                          .setMessage(history.getMessage())
                          .setType(type)
                          .build());
                }
              } else {
                Common.Error error =
                    Common.Error.newBuilder().setMessage("Fail to get history").build();
                historyResponseBuilder.setError(error);
                log.error("Error when get history list of user");
              }
              responseObserver.onNext(historyResponseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  @Override
  public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
    super.transfer(request, responseObserver);
  }
}
