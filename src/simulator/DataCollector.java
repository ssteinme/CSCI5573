package simulator;

public class DataCollector extends java.lang.Thread {

	private static DataCollector singleton = null;

	public static DataCollector getInstance() {
		if (singleton == null) {
			singleton = new DataCollector();
		}
		return singleton;
	}

	private DataCollector() {
	}

	public void CPUidle(CPU aCPU) {
		System.out.println("CPU idle: " + aCPU.toString());
	}
	
	public void CPUinUse(CPU aCPU) {
		System.out.println("CPU in use: " + aCPU.toString() + ", Running thread: " + aCPU.getCurrentThread().toString());		
	}
	
	public void run(CPU aCPU) {
		System.out.println("CPU run");		
	}	
}
