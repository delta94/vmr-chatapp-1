package com.anhvan.vmr.configs;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class VertxConfig {
  private int numOfWorkerThread;
}
