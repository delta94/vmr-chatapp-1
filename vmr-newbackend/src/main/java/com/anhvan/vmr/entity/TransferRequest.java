package com.anhvan.vmr.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferRequest {
  private long sender;
  private long receiver;
  private long amount;
  private String message;
  private long requestId;
  private String password;
}
