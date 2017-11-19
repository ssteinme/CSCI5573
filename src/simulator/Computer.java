package simulator;

import java.util.Random;
import java.util.Vector;

import algorithm.RoundRobinScheduler;
import simulator.event.*;

public class Computer extends java.lang.Thread {
    private int ID_ = -1;
	private Vector<CPU> CPUs_;
	private Vector<Process> processes_ = new Vector<Process>();
	private ThreadScheduler scheduler_ = null; //new RoundRobinScheduler();
	
	public Computer(int id, int nCPUs) {
		this(id, nCPUs, new RoundRobinScheduler());
	}
	public Computer(int id, int nCPUs, ThreadScheduler scheduler) {
		scheduler_ = scheduler;
		ID_ = id;
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

    public int getID_() {
		return ID_;
	}

	public synchronized void startApp() {
		if (processes_.size() < Global.MAX_PROCESSES) {
			Process p = new Process();
			processes_.add(p);		
			scheduler_.setToReady(p);
			//System.out.println("Start application " + p.toString());
		} else {
		    System.out.println(">>>>>>>>>>>>> At maximum allowable number of processes: " + Global.MAX_PROCESSES);			
		}
	}
	
	public synchronized void terminateApp() {
		// Randomly pick a process to terminate
		if (processes_.size() == 0) {
		    System.out.println(">>>>>>>>>>>>> No process to terminate");			
			return;
		}
		Random rand = new Random(System.currentTimeMillis());					
		int idx = rand.nextInt(processes_.size());
		Process p = processes_.get(idx);
		scheduler_.unsubmit(p);
		processes_.remove(p);
		System.out.println("--------- Terminated application " + p.toString());
	}
	
	public void run() {
		System.out.println(getName() + " started.");
		while (true) {
			Event event = EventSimulator.eventQueue.remove();
			if (event instanceof StartApp) {
				startApp();
			} else if (event instanceof TerminateApp) {
				terminateApp();
			}
		}
	}
}
