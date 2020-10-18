package com.anhvan.vmr.service;

import com.anhvan.vmr.entity.TimeTracker;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrackerServiceImpl implements TrackerService {
  private MeterRegistry meterRegistry;

  private Map<String, Timer> timerMap = new ConcurrentHashMap<>();

  public TrackerServiceImpl(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  public TimeTracker getTimeTracker(String metricName, String... tags) {
    String key = getKey(metricName, tags);
    Timer timer = timerMap.get(key);
    if (timer == null) {
      timer = meterRegistry.timer(metricName, tags);
      timerMap.put(key, timer);
    }
    return new TimeTracker(timer);
  }

  private String getKey(String metricName, String... tags) {
    return metricName + ":" + String.join(".", tags);
  }
}
