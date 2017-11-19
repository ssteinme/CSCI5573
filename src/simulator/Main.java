package simulator;

import java.util.Iterator;
import java.util.Vector;

import simulator.event.EventSimulator;

public class Main {
	public Main() {
	}

	public static void main(String[] args) {
		int nCPUs = 1;
		if (args.length > 0) {
	       nCPUs = Integer.parseInt(args[0]);
		}
		EventSimulator eventSimulator = new EventSimulator();
		Global._computers = new Vector<Computer>();
		int nComputers = 1;
		for (int id = 0; id < nComputers; ++id) {
			Computer computer = new Computer(id, nCPUs);
		    Global._computers.add(computer);
			computer.start();
			//java.lang.Thread.yield();
		}		
		eventSimulator.start();
		
		
		try {
			java.lang.Thread.sleep(2*1000);   // in milliseconds
			eventSimulator.join();
			Iterator<Computer> it = Global._computers.iterator();
			while (it.hasNext()) {
				try {
				    it.next().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
	}

}
