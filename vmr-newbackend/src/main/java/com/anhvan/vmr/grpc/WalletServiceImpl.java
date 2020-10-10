package com.anhvan.vmr.grpc;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.database.WalletDatabaseService;
import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.HistoryItemResponse;
import com.anhvan.vmr.entity.WebSocketMessage;
import com.anhvan.vmr.exception.TransferException;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.Common;
import com.anhvan.vmr.proto.Wallet.*;
import com.anhvan.vmr.proto.WalletServiceGrpc;
import com.anhvan.vmr.websocket.WebSocketService;
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
  private WebSocketService webSocketService;
  private ChatCacheService chatCacheService;

  @Override
  public void getBalance(Common.Empty request, StreamObserver<BalanceResponse> responseObserver) {
    long userId = Long.parseLong(GrpcKey.USER_ID_KEY.get());

    // Create response builder
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
                        .setMessage("Could not get balance")
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
                List<HistoryItemResponse> historyList = ar.result();
                log.debug("historyList: {}", historyList);
                for (HistoryItemResponse history : historyList) {
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

    // Create database transfer request to update database
    DatabaseTransferRequest transferRequest =
        DatabaseTransferRequest.builder()
            .sender(userId)
            .receiver(request.getReceiver())
            .amount(request.getAmount())
            .password(request.getPassword())
            .message(request.getMessage())
            .requestId(request.getRequestId())
            .build();

    // Create response builder object
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

                // Response to user
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();

                // Send message to websocket
                Message message =
                    Message.builder()
                        .timestamp(dbResponse.getLastUpdated())
                        .senderId(userId)
                        .receiverId(request.getReceiver())
                        .message(request.getAmount() + ";" + request.getMessage())
                        .type("TRANSFER")
                        .build();

                // Cache the message
                chatCacheService.cacheMessage(message);

                // Send to receiver
                webSocketService.sendTo(
                    request.getReceiver(),
                    WebSocketMessage.builder().type("CHAT").data(message).build());

                // Sendback to sender
                webSocketService.sendTo(
                    userId, WebSocketMessage.builder().type("SEND_BACK").data(message).build());
              } else {
                // Transfer failed
                Throwable cause = ar.cause();
                Common.Error.Builder errorResBuilder = Common.Error.newBuilder();

                // Log the error
                log.error(
                    "Error when transfer: sender={}, recevier={}, amount={}",
                    userId,
                    request.getReceiver(),
                    request.getAmount(),
                    cause);

                // Get error and response to user
                if (cause instanceof TransferException) {
                  TransferException transferException = (TransferException) cause;
                  errorResBuilder.setCode(transferException2ErrorCode(transferException));
                } else {
                  errorResBuilder.setCode(Common.ErrorCode.INTERNAL_SERVER_ERROR);
                }
                responseBuilder.setError(errorResBuilder.build());

                // Send the error
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
              }
            });
  }

  // This method convert database transfer exception into error code
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

  // Convert history model object into grpc history response
  private HistoryResponse.Item history2HistoryResponseItem(HistoryItemResponse history) {
    HistoryResponse.Type type = HistoryResponse.Type.TRANSFER;

    if (history.getType() == HistoryItemResponse.Type.RECEIVE) {
      type = HistoryResponse.Type.RECEIVE;
    }

    return HistoryResponse.Item.newBuilder()
        .setId(history.getId())
        .setSender(history.getSender())
        .setReceiver(history.getReceiver())
        .setAmount(history.getAmount())
        .setBalance(history.getBalance())
        .setMessage(history.getMessage())
        .setTimestamp(history.getTimestamp())
        .setType(type)
        .build();
  }
}
