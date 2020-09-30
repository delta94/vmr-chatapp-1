package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.History;
import io.vertx.core.Future;

import java.util.List;

public interface WalletDatabaseService {
  Future<List<History>> getHistory(long userid);

}
