package cn.easyhbase.common.util;

import org.apache.hadoop.hbase.classification.InterfaceAudience;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Abstract class which contains a Thread and delegates the common Thread
 * methods to that instance.
 * 
 * The purpose of this class is to workaround Sun JVM bug #6915621, in which
 * something internal to the JDK uses Thread.currentThread() as a monitor
 * lock. This can produce deadlocks like HBASE-4367, HBASE-4101, etc.
 */
@InterfaceAudience.Private
public abstract class HasThread implements Runnable {
  private final Thread thread;
  
  public HasThread() {
    this.thread = new Thread(this);
  }

  public HasThread(String name) {
    this.thread = new Thread(this, name);
  }
  
  public Thread getThread() {
    return thread;
  }
  
  public abstract void run();
  
  //// Begin delegation to Thread
  
  public final String getName() {
    return thread.getName();
  }

  public void interrupt() {
    thread.interrupt();
  }

  public final boolean isAlive() {
    return thread.isAlive();
  }

  public boolean isInterrupted() {
    return thread.isInterrupted();
  }

  public final void setDaemon(boolean on) {
    thread.setDaemon(on);
  }

  public final void setName(String name) {
    thread.setName(name);
  }

  public final void setPriority(int newPriority) {
    thread.setPriority(newPriority);
  }

  public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
    thread.setUncaughtExceptionHandler(eh);
  }

  public void start() {
    thread.start();
  }
  
  public final void join() throws InterruptedException {
    thread.join();
  }

  public final void join(long millis, int nanos) throws InterruptedException {
    thread.join(millis, nanos);
  }

  public final void join(long millis) throws InterruptedException {
    thread.join(millis);
  }
  //// End delegation to Thread
}