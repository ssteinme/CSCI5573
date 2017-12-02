/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.prepare.ThreadScheduler;
import simulator.CPU;

/**
 * This mimics a round robin scheduler, but purposefully only uses the first 
 * two cores of the computer and heavily favors the first core.
 * 
 * @author Shannon
 */
public class TwoCoreBiasRoundRobinScheduler extends ThreadScheduler {

  private int myLastCPU = -1;
  private long myLastThreadID = -1;
    
	public TwoCoreBiasRoundRobinScheduler(int nCpus) { super(nCpus); }
  
	public void run() {
		System.out.println(getName() + " started.");
    
    double zero = 0;
    double cnt = 0;
    double one = 0;
    
    long nxt = System.currentTimeMillis() + 5000;
    
		while (!Thread.currentThread().isInterrupted()) {
			CPU idleCPU = null;
			simulator.Process thread = null;
      
			try {
				// idleCPU = getIdleCPU();
        double v = Math.random();
        idleCPU = (v > .1d)?getCPU(0):getCPU(1);
        cnt++;
        
        if(v > .1d)
          zero++;
        else
          one++;
        
        if(System.currentTimeMillis() > nxt) {
          System.out.println("Zero: " + (zero/cnt) + " One: " + (one/cnt));
          nxt = System.currentTimeMillis() + 5000;
          }
                
				thread = getReadyThread();
				idleCPU.run(thread);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
  
  }
