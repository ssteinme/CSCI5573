package prepare;

import core.data.Schedule;
import core.data.TimeSample;
import core.math.NumberTheory;
import core.math.Primes;
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
    TimeSample[] samples = ScheduleSampler.instance().getSamples();
    float NORM = SystemTuning.NORMALIZATION_RANGE;
    
    int[] remain =  new int[samples.length];
    int[] primes = new int[samples.length];
    for(int i=0;i<samples.length;i++) {
      primes[i] = (int)(samples[i].getDuration()*NORM);
      remain[i] = primes[i];
      }
    
    // Move everything to closest prime number.
    Primes.makeClosestPrime(primes);
    int x = (int)NumberTheory.CRT(remain,primes);
    int smallest = 0;
    
    // Compute the new time slot for each process.
    for(int i=0;i<samples.length;i++) {
      int s = x*primes[i];
      samples[i].setStart(s);
      if(s < smallest)
        smallest = s;
      }
    
    // Normalize them to start at zero.
    for(int i=0;i<samples.length;i++) samples[i].setStart(samples[i].getStart()-smallest);
    
    mySchedule = new Schedule(samples);
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
