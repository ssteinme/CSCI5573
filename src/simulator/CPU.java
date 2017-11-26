package simulator;

import algorithm.prepare.ScheduleSampler;
import algorithm.prepare.ThreadScheduler;
import core.data.TimeSample;
import core.io.Log;

public class CPU extends java.lang.Thread {

  // <editor-fold desc="Private Members">

	private int ID_ = -1;
	private int quantum_ = 1;
	private Process currentThread_;
	
  private int myLastIdleID = -1;
	private int accumulatativeBurstTime_ = 0;
	private int burstTime_ = 0;
	private ThreadScheduler scheduler_;
  // </editor-fold>
	
	public CPU(int id) {
		ID_ = id;
		currentThread_ = null;
	}
  
  public String toString() {
    	String s = "";
    	s = s + ID_ + ", accumulative burst time: " + accumulatativeBurstTime_;
    	return s;    	
    }
	
  public Process getCurrentThread() {
		return currentThread_;
	}
	
  public synchronized boolean isIdle() {
		return (currentThread_ == null);
	}
	
	public int getID() {
		return ID_;
	}
  
	public synchronized Process run(Process aThread) {
    
    // Idle time is over.  Time to process so mark the idle time.
    ScheduleSampler.instance().expire(myLastIdleID);
      
		Process preemptedThread = preempt();
		currentThread_ = null;
		burstTime_ = 0;
    
    aThread.setCPU(this);
		currentThread_ = aThread;
    
    // Run whatever code the thing uses.
    DataCollector.getInstance().CPUinUse(this);
    
    //  Execute the instructions.
    if(aThread.isExecuting()) {
      int m = ScheduleSampler.instance().mark(aThread.getID(),TimeSample.eSource.Thread);
      aThread.getCode().executeNext();
      ScheduleSampler.instance().expire(m);
      }
    
    // Ready the idle time until we get called again.
    myLastIdleID =  ScheduleSampler.instance().mark(ID_, TimeSample.eSource.Core);
    
		return preemptedThread;
    }
	
	public synchronized Process preempt() {
    
		if (currentThread_ == null) {
			return null;
      }
    
		accumulatativeBurstTime_ += burstTime_;
		currentThread_.addBurstTime(burstTime_);
		currentThread_.setCPU(null);
		DataCollector.getInstance().CPUidle(this);
		Process preemptedThread = currentThread_;
		currentThread_ = null;
		this.scheduler_.cpuIdle(this);
		return preemptedThread;
	}
	
  /**
   * This is the java.Thread that runs CPU instruction code.
   */
	public void run() {
		while (true) {
			while (isIdle());
      
      synchronized(this) {  // TODO: synchronize this block
        burstTime_ += 1;
        if (burstTime_ == this.quantum_) {
          Process premptedThread = preempt();
          if (premptedThread != null) {
              scheduler_.submit(premptedThread);	
          }
        }
      }
		}
	}
  
	public void setScheduler(ThreadScheduler scheduler) {
		scheduler_ = scheduler;
	}
	
}
