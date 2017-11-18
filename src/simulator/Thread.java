package simulator;

public class Thread {
    private static int _nextId = 0;
    
    private static int getNextId() {
    	return _nextId++;
    }
    private int burstTime_ = 0;
    private int remainedBurstTime_ = 0;
    private long completionTime_ = -1;
    private PCB PCB_;
    private CPU CPU_;
    
	public Thread() {
		PCB_ = new PCB();
		PCB_.setID(getNextId());
	}
	public String toString() {
		return PCB_.toString() + ", burstTime = " + burstTime_;
	}
	
	public void addBurstTime(int n) {
		burstTime_ += n;
	}
	public CPU getCPU() {
		return CPU_;
	}
    
	public void setCPU(CPU cpu) {
		CPU_ = cpu;
	}

	public long waitingTime() {
		return (turnAroundTime() - PCB_.getStartTime());
	}
	
	public long turnAroundTime() {
		return (completionTime_ - PCB_.getStartTime());
	}
	
	public void start() {
		PCB_.setStartTime(System.currentTimeMillis());
	}
    public void timeRun(int ms) {
    	
    }
	public void terminate() {
		
	}
	
	public void destroy() {
		
	}
	
	public void run() {
		
	}
	
	public void preempt() {
		
	}

	public void io() {
		
	}

	public boolean isExecuting() {
		return (CPU_ != null);
	}
}
