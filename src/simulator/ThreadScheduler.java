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
	protected Queue<Thread> threads_ = new LinkedList<Thread>();
	protected Collection<CPU> CPUs_ = new Vector<CPU>();
	
	private Lock threadsLock = new ReentrantLock();
	private Condition threadReady = threadsLock.newCondition();     

	private Lock cpusLock = new ReentrantLock();
	private Condition cpuIdle = cpusLock.newCondition();     
	
	
	public ThreadScheduler() {
	}
	public void setCPUs(Collection<CPU> aCPUs) {
		CPUs_ = aCPUs;
	}
	public void submit(Thread thread) {
		threadsLock.lock();
		try {
			threads_.add(thread);
			threadReady.signal();
		} finally {
			threadsLock.unlock();
		}
	}
	
	public void cpuIdle() {
		cpusLock.lock();
		try {
			cpuIdle.signal();
		} finally {
			cpusLock.unlock();
		}
	}
	protected Thread getReadyThread() throws InterruptedException {
		threadsLock.lock();
		try {
			while (threads_.isEmpty()) {
				threadReady.await();
			}
			return threads_.poll();
		} finally {
			threadsLock.unlock();
		}
	}
	public void unsubmit(Thread thread) {
		threadsLock.lock();
		threads_.remove(thread);
		if (thread.isExecuting()) {
			thread.getCPU().preempt();			
		}
		threadsLock.unlock();
	}
	
	protected CPU getIdleCPU() throws InterruptedException {
		cpusLock.lock();
		try {
			CPU cpu = null;
			do {
				Iterator<CPU> iterator = CPUs_.iterator();
		        while (iterator.hasNext()) {
		        	cpu = iterator.next();
		        	if (cpu.isIdle()) {
		        		return cpu;
		        	}
		        }
		        cpuIdle.await();
			} while (true);
		} finally {
			cpusLock.unlock();
		}
	}
}
