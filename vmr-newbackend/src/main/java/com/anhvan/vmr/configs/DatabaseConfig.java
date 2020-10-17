package com.anhvan.vmr.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {
  private String host;
  private int port;
  private String username;
  private String password;
  private String database;
  private int poolSize;
  private int timeout;
  private int queueSize;
}
