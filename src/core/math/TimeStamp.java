/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.math;

/**
 * Allows for high granularity timing.
 * @author Shannon
 */
public class TimeStamp {

  // <editor-fold desc="Private Members">
  private long myMS;
  private long myNS;
  private double myTime;
  // </editor-fold>
  
  private TimeStamp() {
    myNS = System.nanoTime();
    myMS = System.currentTimeMillis();
    }
  
  /**
   * Mark a timestamp.
   */
  public static TimeStamp mark() {
    return new TimeStamp();
    }

  /**
   * End a timestamp.
   * @return The total time elapsed in milliseconds.
   */
  public static double expire(TimeStamp ts) {
    long ms = System.currentTimeMillis() - ts.myMS;
    long ns = System.nanoTime() - ts.myNS;
    
    if(ms == 0)
      ts.myTime = ns*Conversions.NS_TO_MS;
    else
      ts.myTime = ms;
    
    return ts.myTime;
    }
  
  /**
   * Get the time (always in milliseconds).
   */
  public double time() { return myTime; }
  }
