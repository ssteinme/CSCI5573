package algorithm;

import algorithm.prepare.ThreadScheduler;
import algorithm.tuning.PerformanceTiming;
import core.io.Log;
import core.math.TimeStamp;
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
    
	public RoundRobinScheduler(int nCpus) { super(nCpus); }
  
	public void run() {
		System.out.println(getName() + " started.");
     
		while (!Thread.currentThread().isInterrupted()) {
			CPU idleCPU = null;
			Process thread = null;
      
			try {
        TimeStamp ts = TimeStamp.mark();
        idleCPU = getIdleCPU();
        thread = getReadyThread();
        PerformanceTiming.ALGORITHM_GUESS_TIME = TimeStamp.expire(ts);
				idleCPU.run(thread);
        } 
      catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}
