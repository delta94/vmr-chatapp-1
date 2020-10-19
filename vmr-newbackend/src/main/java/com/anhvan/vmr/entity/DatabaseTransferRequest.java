package com.anhvan.vmr.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class DatabaseTransferRequest {
  private long sender;
  private long receiver;
  private long amount;
  private String message;
  private long requestId;
}
