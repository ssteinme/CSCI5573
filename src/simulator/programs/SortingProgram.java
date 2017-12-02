/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.programs;

import core.io.Log;
import java.util.ArrayList;
import static java.util.Arrays.sort;
import java.util.Collections;
import java.util.List;
import simulator.Code;

/**
 * This is a program that does various types of sorting.
 * 
 * @author Shannon
 */
public class SortingProgram implements Code {

  // <editor-fold desc="Private">
  private List<String> myArray = new ArrayList<>();
  private int myCount = 0;
  // </editor-fold>

  // <editor-fold desc="Constructors">
  public SortingProgram() {
    for(int i=0;i<500000;i++) myArray.add("" + (int)Math.random()*100);
    }
  // </editor-fold>

  // <editor-fold desc="ProgramInstructions">
  
  @Override
  public String getName() { return "Sorting"; }
  
  @Override
  public void shutdown() {}
  
  @Override
  public boolean executeNext() {
    
    try {
      Collections.shuffle(myArray);
      Collections.sort(myArray);
      return (myCount++ < 10);
      } 
    catch (Exception ex) {
      Log.error(ex);
      }
    
    return false;
    }
  // </editor-fold>
  
  }
