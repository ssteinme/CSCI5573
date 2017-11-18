/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prepare;

import core.data.TimeSample;

/**
 * This class is a listener interface provided by the {@link ScheduleSampler} class.
 * @author Shannon
 */
public interface SampleListener {
  
  /**
   * When a new time sample is provided this is called.
   */
  public void sampleAdded(TimeSample ts);
  }
