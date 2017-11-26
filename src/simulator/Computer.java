package simulator;

import algorithm.prepare.ThreadScheduler;
import java.util.Random;
import java.util.Vector;

import algorithm.RoundRobinScheduler;
import algorithm.prepare.ScheduleSampler;
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
    this(nCpus,new RoundRobinScheduler(),new Class[] { SortingProgram.class });
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
      if (processes_.size() < Global.MAX_PROCESSES) {
        myNextThreadID = (myNextThreadID + 1) % (processes_.size()+1);
        
        // Get a random program.
        Code code = (Code)myAvailablePrograms[((int)(Math.random()*myAvailablePrograms.length)+1) % myAvailablePrograms.length].newInstance();
        Log.info("Loading application \"" + code.getName() + "\" Program PID: " + myNextThreadID);
        Process p = new Process(myNextThreadID);
        p.setCode(code);
        processes_.add(p);
        scheduler_.submit(p);
        }
      else
        System.out.println(">>>>>>>>>>>>> At maximum allowable number of processes: " + Global.MAX_PROCESSES);			
      } 
    catch (Exception ex) {
      Log.error(ex);
      }
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
		Log.info("Terminating application " + code.getName() + " PID: " + p.getID());
	}
	
	public void run() {
		System.out.println(getName() + " started.");
    
    long next = System.currentTimeMillis() + 1000;
    
		while (true) {
			Event event = EventSimulator.eventQueue.remove();
			if (event instanceof StartApp) {
				startApp();
			} else if (event instanceof TerminateApp) {
				terminateApp();
        
      if(System.currentTimeMillis() > next) {
        Log.info("Computer: " + getID() + " " + ScheduleSampler.instance().getReport());
        next = System.currentTimeMillis() + 1000;
        }
			}
		}
	}
  // </editor-fold>
  
  public int getID() { return ID_; }
  }
