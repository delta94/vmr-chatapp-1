package com.anhvan.vmr.database;

import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
class TransferStateHolder {
  private volatile SqlConnection conn;
  private volatile Transaction transaction;
  private long receiverId;
  private long senderId;
  private long requestId;
  private long amount;
  private long senderBalance;
  private long receiverBalance;
  private long newSenderBalance; // after update balance
  private long newReceiverBalance; // after update balance
  private long lastUpdated;
  private String message;
  private long transferId;
  private String password;
  private long lastMessageId;
}
