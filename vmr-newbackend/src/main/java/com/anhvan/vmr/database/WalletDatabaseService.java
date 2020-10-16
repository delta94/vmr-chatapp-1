package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.HistoryItemResponse;
import io.vertx.core.Future;

import java.util.List;

public interface WalletDatabaseService {
  Future<List<HistoryItemResponse>> getHistory(long userid);

  Future<List<HistoryItemResponse>> getHistoryWithOffset(long userId, long offset);

  Future<DatabaseTransferResponse> transfer(DatabaseTransferRequest databaseTransferRequest);
}
