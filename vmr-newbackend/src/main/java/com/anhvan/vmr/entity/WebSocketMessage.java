package com.anhvan.vmr.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class WebSocketMessage {
  public enum Type {
    CHAT,
    SEND_BACK,
    ONLINE,
    OFFLINE,
    ACCEPT,
    REMOVE_FRIEND
  }

  private String type;
  private Object data;
}
