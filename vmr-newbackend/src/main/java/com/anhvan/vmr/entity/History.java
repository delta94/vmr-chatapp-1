package com.anhvan.vmr.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class History {
  public enum Type {
    TRANSFER,
    RECEIVE
  }

  private long id;
  private long userId;
  private long sender;
  private long receiver;
  private long amount;
  private long timestamp;
  private long balance;
  private String message;
  private Type type;
}
