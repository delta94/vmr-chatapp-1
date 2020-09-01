package com.anhvan.vmr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage {
  private long id;
  private String type;
  private Integer senderId;
  private Integer receiverId;
  private String message;
  private long timestamp;
}
