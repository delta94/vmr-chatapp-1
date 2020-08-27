package com.anhvan.vmr.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
  private String username;
  private String password;
  private String name;
  private boolean isActive;
}
