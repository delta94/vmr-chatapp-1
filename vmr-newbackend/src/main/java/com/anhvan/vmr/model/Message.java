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
  private long senderId;

  @ColName("receiver")
  private long receiverId;

  private String message;

  @ColName("send_time")
  private long timestamp;

  @ColName("type")
  private String type;

  @ColName("transfer_id")
  private long transferId;
}
