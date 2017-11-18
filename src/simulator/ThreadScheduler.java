package simulator;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Collection;
import java.util.Iterator;

import simulator.Thread;

public abstract class ThreadScheduler extends java.lang.Thread {
	protected int quantum_ = 1;
	protected Queue<Thread> readyQueue = new LinkedList<Thread>();
	protected Queue<Thread> runningQueue = new LinkedList<Thread>();
	protected Vector<CPU> CPUs_ = new Vector<CPU>();
	
	private Lock readyQueueLock = new ReentrantLock();
	private Condition threadReady = readyQueueLock.newCondition();     

	private Lock cpusLock = new ReentrantLock();
	private Condition cpuIdle = cpusLock.newCondition();     
	
	
	public ThreadScheduler() {
	}
	public void setCPUs(Vector<CPU> aCPUs) {
		CPUs_ = aCPUs;
	}
	public void setToReady(Thread thread) {
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
	
	public void cpuIdle(CPU cpu) {
		cpusLock.lock();
		try {
			CPUs_.addElement(cpu);
			cpuIdle.signal();
		} finally {
			cpusLock.unlock();
		}
	}
	protected Thread getReadyThread() throws InterruptedException {
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
	public void submit(Thread thread) {
		setToReady(thread);
	}
	public void unsubmit(Thread thread) {
		readyQueueLock.lock();
		if (thread.isExecuting()) {
			thread.getCPU().preempt();			
		} else {
			readyQueue.remove(thread);
		}
		readyQueueLock.unlock();
	}
	
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
}
