package com.anhvan.vmr.entity;

import com.anhvan.vmr.model.ColName;
import com.anhvan.vmr.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrpcUserResponse extends User {
  @ColName("status")
  private String friendStatus;

  @ColName("last_message")
  private String lastMessage;

  @ColName("last_msg_sender")
  private long lastMessageSenderId;
}
