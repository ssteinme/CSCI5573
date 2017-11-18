package simulator;

import simulator.Thread;

public class CPU extends java.lang.Thread {
	private int ID_ = -1;
	private int quantum_ = 1;
	private Thread currentThread_;
	
	private int accumulatativeBurstTime_ = 0;
	private int burstTime_ = 0;
	private ThreadScheduler scheduler_;
	
	
	public CPU(int id) {
		ID_ = id;
		currentThread_ = null;
	}
    public String toString() {
    	String s = "";
    	s = s + ID_ + ", accumulative burst time: " + accumulatativeBurstTime_;
    	return s;    	
    }
	public Thread getCurrentThread() {
		return currentThread_;
	}
	public synchronized boolean isIdle() {
		return (currentThread_ == null);
	}
	
	public int getID() {
		return ID_;
	}
	public synchronized Thread run(Thread aThread) {
		Thread preemptedThread = preempt();
		currentThread_ = null;
		burstTime_ = 0;
		aThread.setCPU(this);
		currentThread_ = aThread;
		DataCollector.getInstance().CPUinUse(this);
		//System.out.println("Running thread: " + aThread.toString());
		return preemptedThread;
	}
	
	public synchronized Thread preempt() {
		if (currentThread_ == null) {
			return null;
		}
		accumulatativeBurstTime_ += burstTime_;
		currentThread_.addBurstTime(burstTime_);
		currentThread_.setCPU(null);
		DataCollector.getInstance().CPUidle(this);
		Thread preemptedThread = currentThread_;
		currentThread_ = null;		
		this.scheduler_.cpuIdle();
		//System.out.println("Preempted thread: " + preemptedThread.toString());
		return preemptedThread;
	}
	
	public void run() {
		System.out.println(getName() + " started.");
		while (true) {
			while (isIdle());
			try {
				java.lang.Thread.sleep(1000);   // 1000 ms = 1s
				synchronized(this) {  // TODO: synchronize this block
					burstTime_ += 1;
					if (burstTime_ == this.quantum_) {
						Thread premptedThread = preempt();
						scheduler_.submit(premptedThread);					
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
		}
	}
	public void setScheduler(ThreadScheduler scheduler) {
		scheduler_ = scheduler;
	}
	
}
