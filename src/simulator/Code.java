package simulator;

import simulator.CPU;

/**
 * This is implemented by anyone that wants to simulate CPU instruction
 * code.  Think of this as a line-by-line executor of some machine
 * code run by a machine thread.
 * 
 * @author Shannon
 */
public interface Code {
  
  /**
   * Called to execute the "next" line of code in this
   * virtual hardware component.
   * 
   * This can be any action (or sequence of actions) as desired by
   * the implementation to simulate hardware functions.
   * 
   * @return true when this "process" wants to exit.
   */
  public boolean executeNext();
  
  /**
   * Called when this instruction set should terminate execution permanently.
   * This method SHOULD NOT modify the process Identifier or CPU.  It should
   * only shut down any processing needs and reset internal data if this
   * process is executed again.
   */
  public void shutdown();

  /**
   * Get the program name.
   */
  public String getName();
  
  // </editor-fold>
  }





