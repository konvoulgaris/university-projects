package utils;

public class Timer {
  private long start = 0;
  private long stop = 0;
  private long elapsed = 0;

  public Timer() { }

  public void start() {
    start = System.nanoTime();
    stop = 0;
    elapsed = 0;
  }

  public void stop() {
    stop = System.nanoTime();
    elapsed = stop - start;
  }

  public double getNanoseconds() {
    return (double) elapsed;
  }

  public double getMicroseconds() {
    return getNanoseconds() / 1000;
  }

  public double getMilliseconds() {
    return getMicroseconds() / 1000;
  }
}
