package com.anhvan.vmr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Message {
  private long id;

  @ColName("sender")
  private int senderId;

  @ColName("receiver")
  private int receiverId;

  private String message;

  @ColName("send_time")
  private long timestamp;
}
