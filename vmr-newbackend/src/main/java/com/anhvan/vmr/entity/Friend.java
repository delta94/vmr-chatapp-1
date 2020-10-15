package com.anhvan.vmr.entity;

import com.anhvan.vmr.model.ColName;
import com.anhvan.vmr.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class Friend extends User {
  public enum Status {
    WAITING,
    NOT_ANSWER,
    ACCEPTED,
    NOTHING,
    REMOVED
  }

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
