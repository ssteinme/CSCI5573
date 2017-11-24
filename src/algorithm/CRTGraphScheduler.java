/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.prepare.SampleListener;
import algorithm.prepare.ScheduleSampler;
import algorithm.tuning.PerformanceTiming;
import core.data.Schedule;
import core.data.TimeSample;
import core.io.Log;
import core.math.NumberTheory;
import core.math.Primes;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This provides a scheduler algorithm that utilizes a CRT guessing
 * scheme combined with a directed graph shortest path technique that
 * selects optimal schedule.
 * 
 * This class assumes that the {@link SampleListener} class is properly plugged into
 * the framework recording processing times.
 * 
 * @author Shannon S.
 */
public class CRTGraphScheduler {

  // <editor-fold desc="Private Members">
  // Current number of samples taken.
  private int myCount = 0;
  private Schedule mySchedule = null;
  private boolean myNeedsSchedule = true;
  // </editor-fold>
  
  // <editor-fold desc="Private Util">
  
  /**
   * This is the method that generates a schedule.
   */
  private void makeSchedule() {
    TimeSample[] cpuTimes;
    TimeSample[] threadTimes;
    long st = System.currentTimeMillis();
    long et = 0;
    PerformanceTiming.CRT_GUESS_TIME = st;
    TimeSample[] threadSamples = ScheduleSampler.instance().getSamples(TimeSample.eSource.Thread);
    TimeSample[] cores = ScheduleSampler.instance().getSamples(TimeSample.eSource.Core);
    
    long[] remain =  new long[threadSamples.length];
    long[] primes = new long[threadSamples.length];
    for(int i=0;i<threadSamples.length;i++) {
      primes[i] = (long)threadSamples[i].getDuration();
      if(primes[i] == 0) primes[i] = 1;
      remain[i] = primes[i];
      }
    
    // Move everything to closest prime number.
    Primes.makeClosestPrime(primes);
    long x = NumberTheory.CRT(remain,primes);
    
    // Compute the new time slot for each process.
    for(int i=0;i<threadSamples.length;i++) {
      long s = Math.abs((x*primes[i]) % remain[i]);
      threadSamples[i].setStart(s);
      }
    
    // Use Crystin algorithm here.
    // Connect the correct CPU to the thread here.
    // Will have to iterate until all threads are used up.
    Log.error("MUST ADD GRAPH!");
    
    // Make the initial schedule 
    mySchedule = new Schedule(null);
    
    // Stamp the time.
    et = System.currentTimeMillis();
    PerformanceTiming.CRT_GUESS_TIME = et - st;
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Private Constructors">
  
  /**
   * Create the scheduler.
   */
  public CRTGraphScheduler() {}
  
  // </editor-fold>
    
  // <editor-fold desc="Schedule Functions">
  
  /**
   * Ask for the current system schedule.
   */
  public Schedule getSchedule() {
    if(myNeedsSchedule)
      makeSchedule();
    return mySchedule;
    }
  
  // </editor-fold>

  /**
   * Testing method that tests the behavior of making a first schedule guess.
   */
  public static void testCRTGraphScheduler() {
    
    try {
      double gt = 0;
      
      for(int k=0;k<5;k++) {
        
        for(int i=0;i<25;i++) {
          int t = ScheduleSampler.instance().mark(k, TimeSample.eSource.Core);
          int r = (int)(Math.random()*200);
          Thread.sleep(r);
          ScheduleSampler.instance().expire(t);
          }
        
        Schedule sch = new CRTGraphScheduler().getSchedule();
        gt += PerformanceTiming.CRT_GUESS_TIME;
        System.out.println("----------------------------------------------------\n");
        System.out.println(sch.toCSVString());
        }
      
      System.out.println("Ave: " + (gt/5) + "ms");
      
      //new JFile("C:\\Users\\Shannon\\Desktop\\test.csv").setText(sch.toCSVString());
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    
    }
  
  public static void main(String[] args) {
      testCRTGraphScheduler();
      }

  }
