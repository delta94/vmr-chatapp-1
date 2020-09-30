package com.anhvan.vmr.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferResponse {
  long newBalance;
  long lastUpdated;
}
