package com.anhvan.vmr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
  private String username;
  private String name;
  private long id;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @ColName("balance")
  private long balance;

  @ColName("last_updated")
  private long lastUpdated;
}
