/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.data;

import java.util.Arrays;
import java.util.Comparator;

/**
 * This holds a preliminary schedule to be used.
 * @author Shannon
 */
public class Schedule {
  
  // <editor-fold desc="Private">
  private TimeSample[] myTimes;
  // </editor-fold>

  // <editor-fold desc="Constructors">
  public Schedule(TimeSample[] times) {
    myTimes = times;
    
    Arrays.sort(times, new Comparator<TimeSample>(){
      public int compare(TimeSample t1, TimeSample t2) { 
        if(t1.getStart() < t2.getStart())
          return -1;
        else if(t1.getStart() > t2.getStart())
          return 1;
        
        return 0; 
        }
      });
    }
  // </editor-fold>
  
  public TimeSample[] getTimes() { return myTimes; }
  
  public String toCSVString() {
    StringBuffer sb = new StringBuffer();
    sb.append("START,DURATION\n");
    for(int i=0;i<myTimes.length;i++) 
      sb.append(myTimes[i].getStart() + "," + myTimes[i].getDuration() + "\r\n");
    return sb.toString();
    }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for(int i=0;i<myTimes.length;i++) sb.append(myTimes[i].toString() + "\r\n");
    return sb.toString();
    }
  
  }
