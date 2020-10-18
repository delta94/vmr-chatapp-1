package com.anhvan.vmr.database;

import com.anhvan.vmr.entity.DatabaseTransferRequest;
import com.anhvan.vmr.entity.DatabaseTransferResponse;
import com.anhvan.vmr.entity.HistoryItemResponse;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.util.AsyncUtil;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class WalletDatabaseServiceWithTrackerImpl extends WalletDatabaseServiceImpl {
  private TimeTracker historyTracker;
  private TimeTracker transferTracker;

  @Override
  public Future<List<HistoryItemResponse>> getHistoryWithOffset(long userId, long offset) {
    Promise<List<HistoryItemResponse>> promise = Promise.promise();
    TimeTracker.Tracker tracker = historyTracker.start();

    super.getHistoryWithOffset(userId, offset)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }

  @Override
  public Future<DatabaseTransferResponse> transfer(DatabaseTransferRequest transferRequest) {
    Promise<DatabaseTransferResponse> promise = Promise.promise();
    TimeTracker.Tracker tracker = transferTracker.start();

    super.transfer(transferRequest)
        .onComplete(
            ar -> {
              AsyncUtil.convert(promise, ar);
              tracker.record();
            });

    return promise.future();
  }
}
