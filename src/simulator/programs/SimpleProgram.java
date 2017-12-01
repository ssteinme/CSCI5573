/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.programs;

import core.io.Log;
import simulator.Code;

/**
 * A simple program that makes a one-up counter.
 * @author Shannon
 */
public class SimpleProgram implements Code {

  private int myCounterThingie = 0;
  
  // <editor-fold desc="Instruction Hardware">

  /**
   * @seeallso {@link Instructions#executeNext()}
   */
  public boolean executeNext() {
    myCounterThingie++;
    if(myCounterThingie > 100)
      return false;
    return true;
    }
    
  /**
   * @seeallso {@link Instructions#executeNext()}
   */
  public void shutdown() {
    myCounterThingie = 0;
    }
  
  public String getName() { return "One-Up"; }
  // </editor-fold>
  
  }
