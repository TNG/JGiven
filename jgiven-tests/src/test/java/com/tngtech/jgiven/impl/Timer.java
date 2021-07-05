package com.tngtech.jgiven.impl;

public class Timer {
  private long startNanoseconds;
  private long elapsedTime = -1L;

  public Timer() {
    startNanoseconds = System.nanoTime();
  }

  public void reset() {
    elapsedTime = -1L;
    startNanoseconds = System.nanoTime();
  }

  public long stop() {
    if (elapsedTime != -1L)
      return elapsedTime;
    elapsedTime = System.nanoTime() - startNanoseconds;
    return elapsedTime;
  }
}
