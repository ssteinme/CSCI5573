package prepare;

import core.data.Schedule;
import core.data.TimeSample;
import tuning.SystemTuning;

/**
 * This class is a utility that attempts to guess a good schedule.
 * 
 * @author Shannon
 */
public class ScheduleGuesser implements SampleListener {

  // <editor-fold desc="Singleton Members">
  private static Object ourMutex = new Object();
  private static ScheduleGuesser ourInst = null;
  // </editor-fold> 
  
  // <editor-fold desc="Private Members">
  // Current number of samples taken.
  private int myCount = 0;
  private Schedule mySchedule = null;
  
  // </editor-fold>
  
  // <editor-fold desc="Private Util">
  
  /**
   * This is the method that generates a schedule.
   */
  private void makeSchedule() {
    
    }
  // </editor-fold>
  
  // <editor-fold desc="Private Constructors">
  
  private ScheduleGuesser() {
    ScheduleSampler.instance().addListener(this);
    }
  
  // </editor-fold>
    
  // <editor-fold desc="Listener to the ScheduleSampler">
  @Override
  public void sampleAdded(TimeSample ts) { 
    myCount++;
    
    if(myCount >= SystemTuning.RE_SCHEDULE_COUNT) {
      // Re-Build a schedule.
      makeSchedule();
      myCount = 0;
      }
    
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Schedule Functions">
  
  /**
   * Ask for the current system schedule.
   */
  public Schedule getSchedule() {
    if(mySchedule == null)
      makeSchedule();
    return mySchedule;
    }
  
  // </editor-fold>

  // <editor-fold desc="Singleton Access">

  /**
   * Get the instance to this class for use.
   */
  public static ScheduleGuesser instance() {
    synchronized(ourMutex) {
      if(ourInst == null) ourInst = new ScheduleGuesser();
      return ourInst;
      }
    }
  // </editor-fold>
  }
