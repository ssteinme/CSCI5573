package algorithm;

import algorithm.prepare.ThreadScheduler;
import core.io.Log;
import simulator.CPU;
import simulator.Process;


public class RoundRobinScheduler extends ThreadScheduler {

  private int myLastCPU = -1;
  private long myLastThreadID = -1;
    
	public RoundRobinScheduler() {}
  
	public void run() {
		System.out.println(getName() + " started.");
    
		while (true) {
			CPU idleCPU = null;
			Process thread = null;
      
			try {
				idleCPU = getIdleCPU();
				thread = getReadyThread();
				idleCPU.run(thread);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}
