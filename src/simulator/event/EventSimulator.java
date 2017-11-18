package simulator.event;

import java.util.*;

import simulator.*;



public class EventSimulator extends java.lang.Thread {
	public static EventQueue eventQueue = new EventQueue();
	public static Set<Event> eventSet;   // Predefined event set
	
	static {
		// We do quick, easy code here:  add 8 StartApp events and 2 TerminateApp event to make 
		// a rate of 20% terminate app event.
		eventSet = new HashSet<Event>();
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new StartApp());
		eventSet.add(new TerminateApp());
		eventSet.add(new TerminateApp());
	}
	
	public EventSimulator() {
		this.setName("Event Simulator");
	}
	
	public void run() {
		System.out.println(getName() + " started.");
		List<Event> asList = new ArrayList<Event>(eventSet);
		Random rand = new Random(System.currentTimeMillis());	
		int noActivityPeriod = 0;
		while (true) {
			try {
				// Sleep until the next random event
				java.lang.Thread.sleep(noActivityPeriod*1000);   // in milliseconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Generate event
			Collections.shuffle(asList);
			Event event = asList.get(0);
			// Add the generated event to eventQueue	
			eventQueue.add(event);
    		noActivityPeriod = rand.nextInt(Global.MAX_NO_ACTIVITY_PERIOD) + 1;   //in seconds
		}
	}
}
