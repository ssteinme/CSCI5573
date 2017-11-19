package simulator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import simulator.event.Event;;

public class EventQueue {

	private Queue<Event> eventQueue = new LinkedList<Event>();  // LinkedList is a synchronized structure
	private Lock lock = new ReentrantLock();
	//final Condition avail  = lock.newCondition(); 
	private Condition notEmpty = lock.newCondition();     
	
	public EventQueue() {
		// TODO Auto-generated constructor stub
	}
	
	public void add(Event event) {
		lock.lock();
		try {
			eventQueue.add(event);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}
	
	public Event remove() {
		Event event = null;
		lock.lock();
		try {
			while (eventQueue.isEmpty()) {
				try {
					notEmpty.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			event = eventQueue.poll();
		} finally {
		    lock.unlock();
		}
		return event; 
	}
}
