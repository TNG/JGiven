package com.tngtech.jgiven.impl;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

public class Timer {
  Stopwatch timer;

  public Timer() {
    this.timer = Stopwatch.createStarted();
  }

  public void reset() {
   this.timer.reset();
  }

  public long stop() {
    timer.stop();
    return timer.elapsed(TimeUnit.MILLISECONDS);
  }
}
