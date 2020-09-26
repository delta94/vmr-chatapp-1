package com.anhvan.vmr.entity;

import com.anhvan.vmr.model.User;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
public class UserResponse {
  private Boolean online;
  private long id;
  private String name;
  private String username;

  public static UserResponse fromUser(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .build();
  }
}
