package simulator;

import algorithm.CRTGraphScheduler;
import algorithm.prepare.ThreadScheduler;
import java.util.Random;
import java.util.Vector;

import algorithm.prepare.ScheduleSampler;
import algorithm.tuning.PerformanceTiming;
import algorithm.tuning.SystemTuning;
import core.io.JFile;
import core.io.Log;
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
  private StringBuffer myResultsData;
  private StringBuffer myPerformanceData;
  
  private int myNCPUs = 0;
  private int myNextId = -1;
  private int myNThreads = 0;
  private Class[] myAvailablePrograms;
  private int ID_ = (++myNextId);
	private Vector<CPU> CPUs_;
	private Vector<Process> processes_ = new Vector<Process>();
	private ThreadScheduler scheduler_ = null; //new RoundRobinScheduler();
	private int myNextThreadID = -1;
  // </editor-fold>
  
  // <editor-fold desc="Constructors">
  public Computer(int nCpus, int threads) {
    this(nCpus, threads, new CRTGraphScheduler(nCpus),new Class[] { SortingProgram.class });
    }

  public Computer(int nCpus, int nThreads, ThreadScheduler sched) {
    this(nCpus, nThreads, sched, new Class[] { SortingProgram.class });
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
	public Computer(int nCPUs, int nthreads, ThreadScheduler scheduler, Class[] programs) {
    myNCPUs = nCPUs;
    myNThreads = nthreads;
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
	
  public void logData() {
    
    // Log Tuning.
    if(myPerformanceData == null)  myPerformanceData = new StringBuffer("Num CPUs, Algorithm Time (ms), CRT Time (ms), Graph Time (ms)\r\n");
    myPerformanceData.append(myNCPUs + "," + PerformanceTiming.ALGORITHM_GUESS_TIME + "," + PerformanceTiming.CRT_TIME + "," + PerformanceTiming.GRAPH_TIME + "\r\n");
    
    if(myResultsData == null) myResultsData = new StringBuffer("Computer ID, Num Threads, CPU ID, Idle Time (ms)\r\n");
    myResultsData.append(ScheduleSampler.instance().toCSVReport("" + getID() + "," + processes_.size()) + "\r\n");
    
    if(myPerformanceData.length() > 256) {
      String fn = "perf_cpus-" + myNCPUs + "_threads-" + processes_.size() + "_" + scheduler_.getClass().getName() + ".csv";
      new JFile("C:\\Users\\Shannon\\Documents\\Baseline_Trunk\\Documents\\School\\CSCI5573 - Operating Systems\\Homework\\Team Project\\" + fn).appendText(myPerformanceData.toString());
      myPerformanceData = new StringBuffer();
      }
    
    if(myResultsData.length() > 256) {
      String fn = "idle_cpus-" + myNCPUs + "_threads-" + processes_.size() + "_" + scheduler_.getClass().getName() + ".csv";
      new JFile("C:\\Users\\Shannon\\Documents\\Baseline_Trunk\\Documents\\School\\CSCI5573 - Operating Systems\\Homework\\Team Project\\" + fn).appendText(myResultsData.toString());
      myResultsData = new StringBuffer();
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
		scheduler_.unsubmit(p);
		processes_.remove(p);
    p.terminate();
    }
	
	public void run() {
		System.out.println(getName() + " started CPUs: " + myNCPUs + " and " + myNThreads + " threads");
    long next = System.currentTimeMillis() + 5000;
    
    for(int i=0;i<myNThreads;i++) {
      startApp();
      Thread.yield();
      }
    
		while (true && !Thread.currentThread().isInterrupted()) {
      Thread.yield();
      
      if(System.currentTimeMillis() > next) {
        logData();
        Log.info(getReport());
        next = System.currentTimeMillis() + 5000;
        }
      }
    
    System.out.println(getName() +" shutdown.");
    scheduler_.interrupt();
    }
  
  // </editor-fold>
  
  public int getID() { return ID_; }
  }
