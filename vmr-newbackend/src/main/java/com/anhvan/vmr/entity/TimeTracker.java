package com.anhvan.vmr.entity;

import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Getter
public class TimeTracker {
  @Getter
  public static class Tracker {
    private long startTime;
    private long executionTime;
    private Timer timer;

    public Tracker(long startTime, Timer timer) {
      this.startTime = startTime;
      this.timer = timer;
    }

    public void record() {
      long time = System.currentTimeMillis() - startTime;
      timer.record(time, TimeUnit.MILLISECONDS);
      executionTime = time;
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
