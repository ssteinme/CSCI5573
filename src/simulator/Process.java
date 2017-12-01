package simulator;

import core.io.Log;

/**
 * This is a process object which defines a "thread" in the system.
 * 
 * This was modified from the name "Thread" to help with the confusion.
 * 
 * @author Shannon
 */public class Process {
  
  // <editor-fold desc="Private Members">
  private int myBurstTime = 0;
  private Code myCode;
  
  private long myID = -1;
  private CPU myCPU = null;
  private CPU myPrevCPU = null;
  
  // </editor-fold>
  
  // <editor-fold desc="Constructors">
	public Process(long id) { myID = id; }
  // </editor-fold>
  
  // <editor-fold desc="Timing">
  
  /**
   * No clue what this measures.
   */
	public void addBurstTime(int n) {
		myBurstTime += n;
    }
  // </editor-fold>
  
  // <editor-fold desc="Properties">

  /**
   * Assign what code this thread should be executing.
   */
  public void setCode(Code pi) { myCode = pi; } 
  
  /**
   * Get the current code this thread is set to execute.
   */
  public Code getCode() { return myCode; }
  
  /**
   * Request which CPU this process/thread is running on.
   */
  public CPU getCPU() {
		return myCPU;
    }
    
  /**
   * Assign the CPU this thread is currently being processed on.
   * This is only assigned if the thread is active.
   * Should be null if not active.
   */
	public void setCPU(CPU cpu) {
    myPrevCPU = myCPU;
    myCPU = cpu;
    }
  
  /**
   * Ask if this thread is currently executing
   * on a CPU.
   */
	public boolean isExecuting() {
		return (myCPU != null);
    }

  /**
   * Get this thread identifier.
   */
  public long getID() {
    return myID;
    }
  // </editor-fold>
  
  /**
   * @seeallso {@link Instructions#executeNext()}
   */
  public void terminate() {
    if(getCode() != null)
      getCode().shutdown();
    myBurstTime = 0;
    myPrevCPU = null;
    }
  
  // </editor-fold>

  // <editor-fold desc="Object">
  @Override
  public String toString() {
    return "PID: " + getID() + " NAME:" + getClass().getName();
    }
  // </editor-fold>
  }
