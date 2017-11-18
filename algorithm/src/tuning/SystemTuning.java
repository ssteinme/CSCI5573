/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tuning;

/**
 * This class provides a set of tuning variables for
 * the entire system.
 * 
 * @author Shannon
 */
public class SystemTuning {
  
  /**
   * Every time duration is multiplied and divided by this value to
   * convert it to an integer range value for CRT prime search.
   */
  public static final int NORMALIZATION_RANGE = 1000;
    
  /**
   * This is the number of time saples to take before
   * we create a new schedule and apply it.
   */
  public static final int RE_SCHEDULE_COUNT = 10000;
  
  /**
   * This is the maximum number of simultaneous timing samples
   * that can be taking place without dropping one.
   */
  public static final int MAX_MARKER_COUNT = 100;
  
  }
