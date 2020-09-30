package com.anhvan.vmr.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class History {
  enum Type {
    TRANSFER,
    RECEIVE
  }

  long id;
  long userId;
  long amount;
  long timestamp;
  Type type;
}
