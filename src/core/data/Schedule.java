/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.data;

import core.io.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This holds a preliminary schedule to be used.
 * @author Shannon
 */
public class Schedule {
  
  // <editor-fold desc="Private">
  private Thread2CPU[] mySchedule;
  
  private int myAvailableThreads = 0;     // How many threads are left that need a home.
  // </editor-fold>

  // <editor-fold desc="Constructors">
  
  /**
   * Create the schedule provided the allocations currently available.
   */
  public Schedule(Thread2CPU[] schedule) {
    mySchedule = schedule;
    
    // Sort all the times by start time.
    Arrays.sort(mySchedule, new Comparator<Thread2CPU>(){
      public int compare(Thread2CPU t1, Thread2CPU t2) { 
        if(t1.THREAD.getStart() < t2.THREAD.getStart())
          return -1;
        else if(t1.THREAD.getStart() > t2.THREAD.getStart())
          return 1;
        return 0; 
        }
      });
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Access to Schedule">

  /**
   * Ask for the next block of threads and their associated CPU.
   * @param nFreeCpus The number of currently available CPU's to the system.
   * @return null when there is nothing more to schedule.
   */
  public Thread2CPU[] getNextBatch(int nFreeCpus) {
    
    try {
      
      // No CPU's are free or we don't have any threads that need a home.
      if(nFreeCpus == 0 || myAvailableThreads == 0)
        return null;
      
      // a)  Grab the first item available
      // b)  Skip over items that have the same start time.
      int c = 0;
      int cnt = nFreeCpus;
      if(cnt > myAvailableThreads)
        cnt = myAvailableThreads;
      
      long last = -1;
      int t = 0;
      Thread2CPU[] t2t = new Thread2CPU[cnt];
      
      while(t < cnt) {
        
        for(int i=0;i<mySchedule.length;i++) {
          if(mySchedule[i].THREAD.getStart() == last) continue;
          t2t[t++] = mySchedule[i];
          mySchedule[i] = null;
          last = mySchedule[i].THREAD.getStart();
          }
        }
      
      return t2t;
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    
    return null;
    }
  // </editor-fold>
  
  // <editor-fold desc="Reporting and Strings">

  /**
   * Get a comma separated report string of the schedule.
   */
  public String toCSVString() {
    StringBuffer sb = new StringBuffer();
    sb.append("START,DURATION\n");
    for(int i=0;i<mySchedule.length;i++) 
      sb.append(mySchedule[i].THREAD.getStart() + "," + mySchedule[i].THREAD.getDuration() + "\r\n");
    return sb.toString();
    }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for(int i=0;i<mySchedule.length;i++) sb.append(mySchedule[i].THREAD.toString() + "\r\n");
    return sb.toString();
    }
  // </editor-fold>
  
  // <editor-fold desc="Internal Classes">
  /**
   * This contains two ID's the thread and the
   * CPU it should be allocated to.
   */
  public class Thread2CPU {
    public int CPU = -1;
    public TimeSample THREAD = null;
    public Thread2CPU(int cpu, TimeSample th) {
      CPU = cpu;
      THREAD = th;
      }
    }
  // </editor-fold>
  
  }
