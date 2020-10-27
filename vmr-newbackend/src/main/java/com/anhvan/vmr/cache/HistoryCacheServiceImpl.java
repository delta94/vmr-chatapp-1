package com.anhvan.vmr.cache;

import com.anhvan.vmr.configs.CacheConfig;
import com.anhvan.vmr.consts.Metric;
import com.anhvan.vmr.entity.HistoryItemResponse;
import com.anhvan.vmr.entity.TimeTracker;
import com.anhvan.vmr.service.AsyncWorkerService;
import com.anhvan.vmr.service.TrackerService;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public class HistoryCacheServiceImpl implements HistoryCacheService {
  private static final String HISTORY_KEY = "vmr:user:%d:history";

  private RedissonClient redissonClient;
  private AsyncWorkerService asyncWorkerService;
  private CacheConfig cacheConfig;
  private TimeTracker cacheTracker;

  public HistoryCacheServiceImpl(
      RedissonClient redissonClient,
      AsyncWorkerService asyncWorkerService,
      CacheConfig cacheConfig,
      TrackerService trackerService) {
    this.asyncWorkerService = asyncWorkerService;
    this.redissonClient = redissonClient;
    this.cacheConfig = cacheConfig;
    cacheTracker = trackerService.getTimeTracker(Metric.CACHE_QUERY_TIME, "method");
  }

  private String getKey(long userId) {
    return String.format(HISTORY_KEY, userId);
  }

  @Override
  public Future<Void> cacheHistory(long userId, List<HistoryItemResponse> historyList) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = cacheTracker.start();

    asyncWorkerService.execute(
        () -> {
          try {
            RList<HistoryItemResponse> cacheList = redissonClient.getList(getKey(userId));
            cacheList.clear();
            cacheList.addAll(historyList);
            cacheList.expire(cacheConfig.getTimeToLive(), TimeUnit.SECONDS);
            promise.complete();
          } catch (Exception e) {
            log.error("Error when cache history list: userId={}", userId);
            promise.fail(e);
          }
          tracker.record();
        });

    return promise.future();
  }

  @Override
  public Future<List<HistoryItemResponse>> getHistory(long userId) {
    Promise<List<HistoryItemResponse>> promise = Promise.promise();
    TimeTracker.Tracker tracker = cacheTracker.start();

    asyncWorkerService.execute(
        () -> {
          try {
            RList<HistoryItemResponse> cacheList = redissonClient.getList(getKey(userId));
            List<HistoryItemResponse> result = cacheList.readAll();
            if (result.size() == 0) {
              promise.fail("Empty cache");
              return;
            }
            promise.complete(result);
          } catch (Exception e) {
            log.error("Error in getHistory: userId={}", userId);
            promise.fail(e);
          } finally {
            tracker.record();
          }
        });

    return promise.future();
  }

  @Override
  public Future<Void> clearHistory(long userId) {
    Promise<Void> promise = Promise.promise();
    TimeTracker.Tracker tracker = cacheTracker.start();

    asyncWorkerService.execute(
        () -> {
          try {
            RList<HistoryItemResponse> cacheList = redissonClient.getList(getKey(userId));
            cacheList.clear();
            promise.complete();
          } catch (Exception e) {
            log.error("Error in clearHistory: userId={}", userId);
            promise.fail(e);
          }
          tracker.record();
        });

    return promise.future();
  }
}
