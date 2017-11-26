package algorithm.prepare;

import core.io.Log;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.Queue;
import simulator.CPU;
import simulator.Process;

/**
 * This is the foundation class for a scheduler algorithm.
 * 
 * This will be run in the CPU as a separate thread and is started
 * to perform scheduling work by placing threads onto the CPU's.
 * 
 * @author Shannon
 * @author Tri
 */
public abstract class ThreadScheduler extends java.lang.Thread {
  
  // <editor-fold desc="Protected">
  
	protected int quantum_ = 1;
	protected Queue<Process> readyQueue = new LinkedList<Process>();
	protected Queue<Process> runningQueue = new LinkedList<Process>();
	protected Vector<CPU> CPUs_ = new Vector<CPU>();
  // </editor-fold>
  
  // <editor-fold desc="Private">
  private Lock readyQueueLock = new ReentrantLock();
	private Condition threadReady = readyQueueLock.newCondition();     
  
	private Lock cpusLock = new ReentrantLock();
	private Condition cpuIdle = cpusLock.newCondition();     
	// </editor-fold>
  
  // <editor-fold desc="Constructors">

	public ThreadScheduler() {
	}
  // </editor-fold>
	
  // <editor-fold desc="CPU/Thread Accessors">

  /**
   * Provide a list of all available CPU's to this scheduler.
   * @param aCPUs 
   */
  public void setCPUs(Vector<CPU> aCPUs) {
		CPUs_ = aCPUs;
	}
	
	public void cpuIdle(CPU cpu) {
		cpusLock.lock();
		try {
			CPUs_.addElement(cpu);
			cpuIdle.signal();
		} finally {
			cpusLock.unlock();
		}
	}
	
  /**
   * Ask for the next thread waiting to do some work.
   */
  protected Process getReadyThread() throws InterruptedException {
		readyQueueLock.lock();
		try {
			while (readyQueue.isEmpty()) {
				threadReady.await();
			}
			return readyQueue.poll();
		} finally {
			readyQueueLock.unlock();
		}
	}
	
  /**
   * Called from the CPU when a new thread is available for processing
   * use.
   */
  public void submit(Process thread) {
		if (thread == null) {
			return;
		}
		readyQueueLock.lock();
		try {
			readyQueue.add(thread);
			threadReady.signal();
		} finally {
			readyQueueLock.unlock();
		}
	}
  
  /**
   * Called from CPU when it's time to kill off this thread
   * as the process/app is dead.
   */
  public void unsubmit(Process thread) {
    readyQueueLock.lock();
		
    if (thread.isExecuting() && thread.getCPU() != null) {
			thread.getCPU().preempt();
		} else {
			readyQueue.remove(thread);
		}
		readyQueueLock.unlock();
	}
	
  /**
   * Ask for the next available CPU that is not busy.
   */
	protected CPU getIdleCPU() throws InterruptedException {
		cpusLock.lock();
		try {
			CPU cpu = null;
			while (CPUs_.size() == 0) {
			    cpuIdle.await();
			}
			cpu = CPUs_.remove(0);
			return cpu;
		} finally {
			cpusLock.unlock();
		}
	}
  // </editor-fold>
  
  // <editor-fold desc="Main Schduler Processing">

  /**
   * Overridden to provide a mechanism to time thread workloads.
   */
  @Override
  public void run() {
    
    try {
      // Do scheduler processing here.
      
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    }
  // </editor-fold>
  
  }
