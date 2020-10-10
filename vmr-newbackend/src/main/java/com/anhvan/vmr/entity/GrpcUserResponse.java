package com.anhvan.vmr.entity;

import com.anhvan.vmr.model.ColName;
import com.anhvan.vmr.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GrpcUserResponse extends User {
  @ColName("status")
  private String friendStatus;

  @ColName("last_message")
  private String lastMessage;

  @ColName("last_message_type")
  private String lastMessageType;

  @ColName("last_message_sender")
  private long lastMessageSenderId;

  @ColName("last_message_timestamp")
  private long lastMessageTimestamp;

  @ColName("num_unread_message")
  private int numUnreadMessage;
}
