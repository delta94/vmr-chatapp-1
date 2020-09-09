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
public class WsMessage {
  private long id;
  private String type;

  @ColName("sender")
  private Integer senderId;

  @ColName("receiver")
  private Integer receiverId;

  private String message;

  @ColName("send_time")
  private long timestamp;
}
