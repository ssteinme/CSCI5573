package simulator;

import java.util.Vector;

public class Global {
	public static Vector<Computer> _computers = new Vector<Computer>();
	public static final int MAX_PROCESSES = 10;
	public static final int MAX_NO_ACTIVITY_PERIOD = 10;  // seconds
	
	public Global() {
    }

}
