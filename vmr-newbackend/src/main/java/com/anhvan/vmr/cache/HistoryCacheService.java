package com.anhvan.vmr.cache;

import com.anhvan.vmr.entity.HistoryItemResponse;
import io.vertx.core.Future;

import java.util.List;

public interface HistoryCacheService {
  Future<Void> cacheHistory(long userId, List<HistoryItemResponse> historyList);

  Future<List<HistoryItemResponse>> getHistory(long userId);

  Future<Void> clearHistory(long userId);
}
