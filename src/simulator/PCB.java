package simulator;

public class PCB {  // Process Control Block PCB
	private int ID;
	private int CPUTimeUsed;
	private long startTime;

	public PCB() {
		ID = -1;
		CPUTimeUsed = 0;
		startTime = System.currentTimeMillis();
	}

	public String toString() {
		String s = "";
		s = s + "ID: " + ID + ", startTime: " + startTime;
		return s;
	}
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCPUTimeUsed() {
		return CPUTimeUsed;
	}

	public void setCPUTimeUsed(int cPUTimeUsed) {
		CPUTimeUsed = cPUTimeUsed;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
