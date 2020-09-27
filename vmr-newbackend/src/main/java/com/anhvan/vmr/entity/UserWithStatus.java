package com.anhvan.vmr.entity;

import com.anhvan.vmr.model.ColName;
import com.anhvan.vmr.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithStatus extends User {
  @ColName("status")
  private String friendStatus;
}
