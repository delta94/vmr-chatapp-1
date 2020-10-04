package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.History;
import io.vertx.core.Future;

import java.util.List;

public interface WalletDatabaseService {
  Future<List<History>> getHistory(long userid);

  Future<DatabaseTransferResponse> transfer(DatabaseTransferRequest databaseTransferRequest);
}