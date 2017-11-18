package algorithm;

import simulator.*;
import simulator.Thread;

public class RoundRobinScheduler extends ThreadScheduler {

	public RoundRobinScheduler() {
	}

	public void run() {
		System.out.println(getName() + " started.");
		while (true) {
			CPU idleCPU = null;
			Thread thread = null;
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
