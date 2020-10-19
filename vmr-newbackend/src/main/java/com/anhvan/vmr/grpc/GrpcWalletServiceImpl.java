package com.anhvan.vmr.grpc;

import com.anhvan.vmr.cache.ChatCacheService;
import com.anhvan.vmr.cache.FriendCacheService;
import com.anhvan.vmr.database.UserDatabaseService;
import com.anhvan.vmr.database.WalletDatabaseService;
import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.HistoryItemResponse;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.exception.TransferException;
import com.anhvan.vmr.model.Message;
import com.anhvan.vmr.model.User;
import com.anhvan.vmr.proto.Common;
import com.anhvan.vmr.proto.Wallet;
import com.anhvan.vmr.proto.Wallet.BalanceResponse;
import com.anhvan.vmr.proto.Wallet.HistoryResponse;
import com.anhvan.vmr.proto.Wallet.TransferRequest;
import com.anhvan.vmr.proto.Wallet.TransferResponse;
import com.anhvan.vmr.proto.WalletServiceGrpc;
import com.anhvan.vmr.util.GrpcUtil;
import com.anhvan.vmr.websocket.WebSocketService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@AllArgsConstructor
@Builder
@Log4j2
public class GrpcWalletServiceImpl extends WalletServiceGrpc.WalletServiceImplBase {
  private UserDatabaseService userDbService;
  private WalletDatabaseService walletDatabaseService;
  private WebSocketService webSocketService;
  private ChatCacheService chatCacheService;
  private FriendCacheService friendCacheService;

  private TimeTracker balanceTracker;
  private TimeTracker historyTracker;
  private TimeTracker transferTracker;

  @Override
  public void getBalance(Common.Empty request, StreamObserver<BalanceResponse> responseObserver) {
    TimeTracker.Tracker tracker = balanceTracker.start();

    // Get request id
    long userId = GrpcKey.getUserId();

    // Log the request
    log.info("Handle getBalance grpc call, userId={}", userId);

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
                // Write log
                log.error("Error when get balance, userId={}", userId, ar.cause());

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
              tracker.record();
            });
  }

  @Override
  public void getHistoryWithOffset(
      Wallet.GetHistoryWithOffsetRequest request,
      StreamObserver<HistoryResponse> responseObserver) {
    TimeTracker.Tracker tracker = historyTracker.start();

    long userId = GrpcKey.getUserId();
    long offset = request.getOffset();

    log.info("Handle getHistory grpc call, userId={}, offset={}", userId, offset);

    HistoryResponse.Builder historyResponseBuilder = HistoryResponse.newBuilder();

    walletDatabaseService
        .getHistoryWithOffset(userId, request.getOffset())
        .onComplete(
            ar -> {
              if (ar.succeeded()) {
                List<HistoryItemResponse> historyList = ar.result();
                HistoryResponse.Data.Builder dataBuilder = HistoryResponse.Data.newBuilder();
                for (HistoryItemResponse history : historyList) {
                  dataBuilder.addItem(GrpcUtil.history2HistoryResponseItem(history));
                }
                historyResponseBuilder.setData(dataBuilder.build());
              } else {
                Common.Error error =
                    Common.Error.newBuilder()
                        .setCode(Common.ErrorCode.INTERNAL_SERVER_ERROR)
                        .setMessage("Fail to get history")
                        .build();
                historyResponseBuilder.setError(error);

                // Write log
                log.error("Error when get history list, userId={}", userId, ar.cause());
              }
              responseObserver.onNext(historyResponseBuilder.build());
              responseObserver.onCompleted();
              tracker.record();
            });
  }

  @Override
  public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
    TimeTracker.Tracker tracker = transferTracker.start();

    long userId = GrpcKey.getUserId();

    // Extract info
    long receiverId = request.getReceiver();
    long amount = request.getAmount();

    log.info(
        "Handle transfer grpc call, userId={}, friendId={}, amount={}, requestId={}",
        userId,
        receiverId,
        amount,
        request.getRequestId());

    // Create response builder object
    TransferResponse.Builder responseBuilder = TransferResponse.newBuilder();

    // Check amount is valid
    if (amount < 1000) {
      Common.Error err =
          Common.Error.newBuilder().setCode(Common.ErrorCode.AMOUNT_NOT_VALID).build();
      responseBuilder.setError(err);
      responseObserver.onNext(responseBuilder.build());
      responseObserver.onCompleted();
      return;
    } else if (userId == receiverId) {
      Common.Error err =
          Common.Error.newBuilder().setCode(Common.ErrorCode.RECEIVER_NOT_EXIST).build();
      responseBuilder.setError(err);
      responseObserver.onNext(responseBuilder.build());
      responseObserver.onCompleted();
      return;
    }

    // Create database transfer request to update database
    DatabaseTransferRequest transferRequest =
        DatabaseTransferRequest.builder()
            .sender(userId)
            .receiver(receiverId)
            .amount(amount)
            .password(request.getPassword())
            .message(request.getMessage())
            .requestId(request.getRequestId())
            .build();

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
                        .receiverId(receiverId)
                        .message(amount + ";" + request.getMessage())
                        .type(Message.Type.TRANSFER.name())
                        .build();

                // Cache the message
                chatCacheService.cacheMessage(message);
                friendCacheService.updateLastMessageForBoth(userId, receiverId, message);
                webSocketService.sendChatMessage(userId, receiverId, message);
              } else {
                // Transfer failed
                Throwable cause = ar.cause();
                Common.Error.Builder errorResBuilder = Common.Error.newBuilder();

                // Log the error
                log.error(
                    "Error when transfer: sender={}, recevier={}, amount={}",
                    userId,
                    receiverId,
                    amount,
                    cause);

                // Get error and response to user
                if (cause instanceof TransferException) {
                  TransferException transferException = (TransferException) cause;
                  errorResBuilder.setCode(GrpcUtil.transferException2ErrorCode(transferException));
                } else {
                  errorResBuilder.setCode(Common.ErrorCode.INTERNAL_SERVER_ERROR);
                }
                responseBuilder.setError(errorResBuilder.build());

                // Send the error
                responseObserver.onNext(responseBuilder.build());
                responseObserver.onCompleted();
              }
              tracker.record();
            });
  }
}
