package com.anhvan.vmr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
  private String username;
  private String name;
  private int id;
  @JsonIgnore private String password;
  @JsonIgnore private boolean active;
}
