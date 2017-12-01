package algorithm;

import algorithm.prepare.ThreadScheduler;
import core.io.Log;
import javax.print.attribute.standard.DateTimeAtCompleted;
import simulator.CPU;
import simulator.Process;


/**
 * This is a perfect round robin that equally gives the thread to each CPU in turn.
 * @author Shannon
 */
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
