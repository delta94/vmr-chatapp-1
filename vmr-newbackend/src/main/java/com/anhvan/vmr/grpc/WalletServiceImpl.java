package com.anhvan.vmr.grpc;

import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.database.WalletDatabaseService;
import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.History;
import com.anhvan.vmr.exception.TransferException;
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
                        .setName(user.getName())
                        .setUserName(user.getUsername())
                        .build();
                responseBuilder.setData(data);
              } else {
                // Get info failue
                Common.Error error =
                    Common.Error.newBuilder()
                        .setCode(Common.ErrorCode.INTERNAL_SERVER_ERROR)
                        .setMessage("Cannot get " + "balance")
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
                  historyResponseBuilder.addItem(history2HistoryResponseItem(history));
                }
              } else {
                Common.Error error =
                    Common.Error.newBuilder()
                        .setCode(Common.ErrorCode.INTERNAL_SERVER_ERROR)
                        .setMessage("Fail to get history")
                        .build();
                historyResponseBuilder.setError(error);
                log.error("Error when get history list of user", ar.cause());
              }
              responseObserver.onNext(historyResponseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  @Override
  public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    DatabaseTransferRequest transferRequest =
        DatabaseTransferRequest.builder()
            .sender(userId)
            .receiver(request.getReceiver())
            .amount(request.getAmount())
            .password(request.getPassword())
            .message(request.getMessage())
            .requestId(request.getRequestId())
            .build();

    TransferResponse.Builder responseBuilder = TransferResponse.newBuilder();

    walletDatabaseService
        .transfer(transferRequest)
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                // Transfer successfully
                DatabaseTransferResponse dbResponse = ar.result();
                responseBuilder.setData(
                    TransferResponse.Data.newBuilder()
                        .setBalance(dbResponse.getNewBalance())
                        .setLastUpdated(dbResponse.getLastUpdated())
                        .build());
              } else {
                // Transfer failed
                Throwable cause = ar.cause();
                Common.Error.Builder errorResBuilder = Common.Error.newBuilder();

                if (cause instanceof TransferException) {
                  TransferException transferException = (TransferException) cause;
                  errorResBuilder.setCode(transferException2ErrorCode(transferException));
                } else {
                  errorResBuilder.setCode(Common.ErrorCode.INTERNAL_SERVER_ERROR);
                }
                responseBuilder.setError(errorResBuilder.build());
              }

              responseObserver.onNext(responseBuilder.build());
              responseObserver.onCompleted();
            });
  }

  private Common.ErrorCode transferException2ErrorCode(TransferException exception) {
    switch (exception.getErrorCode()) {
      case BALANCE_NOT_ENOUGHT:
        return Common.ErrorCode.BALANCE_NOT_ENOUGH;
      case RECEIVER_INVALID:
        return Common.ErrorCode.RECEIVER_NOT_EXIST;
      case REQUEST_EXISTED:
        return Common.ErrorCode.REQUEST_EXISTED;
      case PASSWORD_INVALID:
        return Common.ErrorCode.PASSWORD_INVALID;
    }
    return Common.ErrorCode.INTERNAL_SERVER_ERROR;
  }

  private HistoryResponse.Item history2HistoryResponseItem(History history) {
    HistoryResponse.Type type = HistoryResponse.Type.TRANSFER;

    if (history.getType() == History.Type.RECEIVE) {
      type = HistoryResponse.Type.RECEIVE;
    }

    return HistoryResponse.Item.newBuilder()
        .setId(history.getId())
        .setSender(history.getSender())
        .setReceiver(history.getReceiver())
        .setAmount(history.getAmount())
        .setBalance(history.getBalance())
        .setMessage(history.getMessage())
        .setType(type)
        .build();
  }
}
