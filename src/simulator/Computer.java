package simulator;

import algorithm.CRTGraphScheduler;
import algorithm.prepare.ThreadScheduler;
import java.util.Random;
import java.util.Vector;

import algorithm.prepare.ScheduleSampler;
import algorithm.tuning.SystemTuning;
import core.io.Log;
import simulator.event.*;
import simulator.programs.SortingProgram;

/**
 * This is a computer simulation utility which is itself a java thread.
 * 
 * The computer runs {@link Code}.  Instruction hardware is
 * essentially a {@link Process} that can be executed line by line.
 * 
 * @author Shannon
 * @author Tri
 */
public class Computer extends java.lang.Thread {

  // <editor-fold desc="Private Members">
  private int myNCPUs = 0;
  private int myNextId = -1;
  private Class[] myAvailablePrograms;
  private int ID_ = (++myNextId);
	private Vector<CPU> CPUs_;
	private Vector<Process> processes_ = new Vector<Process>();
	private ThreadScheduler scheduler_ = null; //new RoundRobinScheduler();
	private int myNextThreadID = -1;
  // </editor-fold>
  
  // <editor-fold desc="Constructors">
  public Computer(int nCpus) {
    this(nCpus,new CRTGraphScheduler(nCpus),new Class[] { SortingProgram.class });
    }
  
  /**
   * Create a computer.
   * @param id The identification for this computer.
   * @param nCPUs The number of CPU's/Cores to allow for.
   * @param scheduler The schedule algorithm.
   * @paraam A list of programs {@link Instructions} implementations that
   * should be "randomly" run on this computer.
   * @param programs A list of class names that implement {@link Code}.
   */
	public Computer(int nCPUs, ThreadScheduler scheduler, Class[] programs) {
    myNCPUs = nCPUs;
    myAvailablePrograms = programs;
		scheduler_ = scheduler;
		this.setName("Computer " + ID_);
		scheduler_.setName(getName() + " scheduler");
		CPUs_ = new Vector<CPU>();
		scheduler_.setCPUs(CPUs_);
		for (int i = 0; i < nCPUs; ++i) {
			CPU cpu = new CPU(i);
			cpu.setName(this.getName() + " CPU " + cpu.getID());
			CPUs_.add(cpu);
			cpu.setScheduler(scheduler_);
			cpu.start();
		}
		scheduler_.start();
	}
  // </editor-fold>

  // <editor-fold desc="Application starting/stopping">

	public synchronized void startApp() {
    
    try {
      if (processes_.size() < SystemTuning.MAX_THREADS_PER_CPU) {
        myNextThreadID = (myNextThreadID + 1) % (processes_.size()+1);
        
        // Get a random program.
        Code code = (Code)myAvailablePrograms[((int)(Math.random()*myAvailablePrograms.length)+1) % myAvailablePrograms.length].newInstance();
        Process p = new Process(myNextThreadID);
        p.setCode(code);
        processes_.add(p);
        scheduler_.submit(p);
        }
      } 
    catch (Exception ex) {
      Log.error(ex);
      }
  	}
	
  /**
   * Returns a full report of this computers current status.
   */
  public String getReport() {
    return "Computer: " + getID() + " Threads: " + processes_.size() + " >> " + ScheduleSampler.instance().getReport();
    }
  
	public synchronized void terminateApp() {
    
		// Randomly pick a process to terminate
		if (processes_.size() == 0) 
			return;
    
		Random rand = new Random(System.currentTimeMillis());
		int idx = rand.nextInt(processes_.size());
		Process p = processes_.get(idx);
    Code code = p.getCode();
		scheduler_.unsubmit(p);
		processes_.remove(p);
    p.terminate();
//		Log.info("Terminating application " + code.getName() + " PID: " + p.getID());
    }
	
	public void run() {
		System.out.println(getName() + " started CPUs: " + myNCPUs + " and " + SystemTuning.MAX_THREADS_PER_CPU + " threads");
    long next = System.currentTimeMillis() + 5000;
    
    for(int i=0;i<SystemTuning.MAX_THREADS_PER_CPU;i++) {
      startApp();
      Thread.yield();
      }
    
		while (true) {
      Thread.yield();
      
//			Event event = EventSimulator.eventQueue.remove();
//			if (event instanceof StartApp) {
//				startApp();
//			} else if (event instanceof TerminateApp) {
//				terminateApp();
//        
      if(System.currentTimeMillis() > next) {
        Log.info(getReport());
        next = System.currentTimeMillis() + 5000;
        }
//			}
		}
	}
  // </editor-fold>
  
  public int getID() { return ID_; }
  }
