package simulator;

import algorithm.CRTGraphScheduler;
import algorithm.RoundRobinScheduler;
import algorithm.prepare.ThreadScheduler;
import algorithm.tuning.SystemTuning;
import java.util.Iterator;
import java.util.Vector;

import simulator.event.EventSimulator;

public class Main {
	public Main() {
	}

  /**
   * Run a full test with varying types of parameters.
   * @param cpus Num cpus
   * @param sched The schedler to use
   * @param threads Num threads.
   * @param time The total time (ms) to run.
   */
  private static void runFullTest(int cpus, int threads, ThreadScheduler sched, long time) {
    
    try {
      
      Global._computers = new Vector<Computer>();
      int nComputers = 1;

      for (int id = 0; id < nComputers; ++id) {
        Computer computer = new Computer(cpus,threads,sched);
        Global._computers.add(computer);
        computer.start();
        }
     
      // Run for 5 minutes.
      long next = System.currentTimeMillis() + time;
      while(System.currentTimeMillis() < next) {
        Thread.sleep((int)(.01d*time));
        }
      }
    catch(InterruptedException ex) {
      
      }
    finally {
      try {
        for(int i=0;i<Global._computers.size();i++)
          Global._computers.get(i).interrupt();
        } catch(Exception e) {}
      }
    
    }
  
	public static void main(String[] args) {
    runFullTest(64, 15, new CRTGraphScheduler(64), 60000);
    runFullTest(64, 15, new RoundRobinScheduler(64), 60000);
    }

  }
