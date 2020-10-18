package com.anhvan.vmr.service;

import com.anhvan.vmr.entity.TimeTracker;

public interface TrackerService {
  TimeTracker getTimeTracker(String metricName, String... tags);
}
