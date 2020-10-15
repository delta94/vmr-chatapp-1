package com.anhvan.vmr.config;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ServerConfig {
  private String host;
  private int port;
  private int wsPort;
  private int grpcPort;
  private int prometheusPort;
}
