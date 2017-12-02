package simulator;

import java.util.Vector;

public class Global {
	public static Vector<Computer> _computers = new Vector<Computer>();
	public static final int MAX_NO_ACTIVITY_PERIOD = 2;  // seconds
	public static final int PROCESS_TERMINATION_RATIO = 3;
	public static final int PROCESS_START_RATIO = 7;
  
  // Receives the number of total CPU's for computer in the system on startup.
  public static int MAX_CPU = 0;
	
	public Global() {
    }

}
