/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.data;

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
    }
  // </editor-fold>
  
  public TimeSample[] getTimes() { return myTimes; }
  }
