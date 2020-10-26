package com.anhvan.vmr.util;

import com.anhvan.vmr.entity.HistoryItemResponse;
import com.anhvan.vmr.exception.TransferException;
import com.anhvan.vmr.proto.Common;
import com.anhvan.vmr.proto.Friend;
import com.anhvan.vmr.proto.Wallet;

public class GrpcUtil {
  public static Friend.FriendStatus string2FriendStatus(String status) {
    if (status == null) {
      return Friend.FriendStatus.NOTHING;
    }
    if (status.equals("WAITING")) {
      return Friend.FriendStatus.WAITING;
    }
    if (status.equals("NOT_ANSWER")) {
      return Friend.FriendStatus.NOT_ANSWER;
    }
    if (status.equals("REMOVED")) {
      return Friend.FriendStatus.REMOVED;
    }
    if (status.equals("UNFRIENDED")) {
      return Friend.FriendStatus.UNFRIENDED;
    }
    return Friend.FriendStatus.FRIEND;
  }

  // This method convert database transfer exception into error code
  public static Common.ErrorCode transferException2ErrorCode(TransferException exception) {
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
  public static Wallet.HistoryResponse.Item history2HistoryResponseItem(
      HistoryItemResponse history) {
    Wallet.HistoryResponse.Type type = Wallet.HistoryResponse.Type.TRANSFER;

    if (history.getType() == HistoryItemResponse.Type.RECEIVE) {
      type = Wallet.HistoryResponse.Type.RECEIVE;
    }

    return Wallet.HistoryResponse.Item.newBuilder()
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
