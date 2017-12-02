/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.data;

import core.io.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This holds a preliminary schedule to be used.
 * @author Shannon
 */
public class Schedule {
  
  // <editor-fold desc="Private">
  private Thread2CPU[] mySchedule;
  private int myNext = 0;
  // </editor-fold>

  // <editor-fold desc="Constructors">
  
  /**
   * Create the schedule provided the allocations currently available.
   */
  public Schedule(Thread2CPU[] schedule) {
    mySchedule = schedule;
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Access to Schedule">
  
  /**
   * When there is nothing left to schedule this returns true.
   * This means a new schedule must be created.
   */
  public boolean isEmpty() {
    return myNext >= mySchedule.length;
    }
  
  /**
   * Get the next schedule component.
   */
  public Thread2CPU getNext() {
    if(myNext >= mySchedule.length) return null;
    Thread2CPU tt = mySchedule[myNext++];
    return tt;
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Reporting and Strings">

  /**
   * Get a comma separated report string of the schedule.
   */
  public String toCSVString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CPU, THREAD\n");
    for(int i=0;i<mySchedule.length;i++) sb.append(mySchedule[i].CPU + "," + mySchedule[i].THREAD);
    return sb.toString();
    }
  
  @Override
  public String toString() {
    return toCSVString();
    }
  // </editor-fold>
  
  // <editor-fold desc="Internal Classes">
  /**
   * This contains two ID's the thread and the
   * CPU it should be allocated to.
   */
  public static class Thread2CPU {
    public int CPU = -1;
    public int THREAD = -1;
    
    public Thread2CPU(int cpu, int th) {
      CPU = cpu;
      THREAD = th;
      }
    
    @Override
    public String toString() { 
      return "Thread " + THREAD + " is on CPU " + CPU;
      }
    
    public static void report(Thread2CPU[] cc) {
      System.out.println("------------ Thread2CPU ------------");
      for(Thread2CPU t : cc) 
        System.out.println("   >> " + t.toString());
      }
    
    }
  // </editor-fold>
  
  }
