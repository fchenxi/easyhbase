package cn.easyhbase.common.util;

/**
 * A simple stopwatch
 * @author emeroad
 */
public class StopWatch {
    private long start;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis() - this.start;
    }

}