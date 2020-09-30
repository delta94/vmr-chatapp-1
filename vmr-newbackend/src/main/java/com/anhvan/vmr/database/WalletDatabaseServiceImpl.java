package com.anhvan.vmr.database;

import io.vertx.mysqlclient.MySQLPool;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class WalletDatabaseServiceImpl implements WalletDatabaseService {
  private MySQLPool pool;
}
