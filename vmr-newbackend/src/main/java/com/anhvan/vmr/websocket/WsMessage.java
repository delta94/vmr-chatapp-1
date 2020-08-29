package com.anhvan.vmr.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsMessage {
  private String type;
  private String senderUsername;
  private Integer receiverId;
  private String message;
}
