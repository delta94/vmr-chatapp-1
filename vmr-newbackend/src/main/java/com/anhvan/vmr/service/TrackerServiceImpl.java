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
    // Compute the key
    String key = getKey(metricName, tags);

    // Get timer from map
    Timer timer = timerMap.get(key);

    // If timer not exist
    if (timer == null) {
      timer = meterRegistry.timer(metricName, tags);
      timerMap.put(key, timer);
    }

    // Create time tracker object
    return new TimeTracker(timer);
  }

  public TimeTracker.Tracker start(String metricName, String... tags) {
    // Compute the key
    String key = getKey(metricName, tags);

    // Get timer from map
    Timer timer = timerMap.get(key);

    // If timer not exist
    if (timer == null) {
      timer = meterRegistry.timer(metricName, tags);
      timerMap.put(key, timer);
    }

    // Create time tracker object
    return new TimeTracker(timer).start();
  }

  private String getKey(String metricName, String... tags) {
    return metricName + ":" + String.join(".", tags);
  }
}
