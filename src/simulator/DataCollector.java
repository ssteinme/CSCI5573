package simulator;

import algorithm.prepare.ScheduleSampler;
import core.data.TimeSample;

/**
 * The data collection plugs into the CPU framework and notifies when
 * data is being processed.
 * 
 * @author Shannon
 * @author Tri
 */
public class DataCollector {

  // <editor-fold desc="Private Members">

	private static DataCollector singleton = null;
  // </editor-fold>

  // <editor-fold desc="Singleton">

	public static DataCollector getInstance() {
		if (singleton == null) {
			singleton = new DataCollector();
		}
		return singleton;
	}
  // </editor-fold>

  // <editor-fold desc="Constructors">

	private DataCollector() {
	}
  // </editor-fold>

  // <editor-fold desc="CPU Processign Collection">

  /**
   * Called when the CPU is idle.
   */
	public void CPUidle(CPU aCPU) {
    
    }
	
  /**
   * Called when the CPU is being utilized.
   */
	public void CPUinUse(CPU aCPU) {
    int x = 25;
    }
  // </editor-fold>
	
  }
