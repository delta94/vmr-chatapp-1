package com.anhvan.vmr.entity;

import io.micrometer.core.instrument.Timer;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public class TimeTracker {
  public static class Tracker {
    private long startTime;
    private Timer timer;

    public Tracker(long startTime, Timer timer) {
      this.startTime = startTime;
      this.timer = timer;
    }

    public void record() {
      timer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    }
  }

  private Timer timer;

  public TimeTracker(Timer timer) {
    this.timer = timer;
  }

  public Tracker start() {
    return new Tracker(System.currentTimeMillis(), timer);
  }
}
