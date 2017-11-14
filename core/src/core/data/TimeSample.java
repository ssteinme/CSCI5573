package core.data;

/**
 * This is the main class that holds information about a sample
 * of processing time or idle time.
 * 
 * @author Shannon
 */
public class TimeSample {

  // <editor-fold desc="Private Members">
  private long myStart;
  private float myDur;
  // </editor-fold>
  
  // <editor-fold desc="Constructors">
  
  /**
   * Create a sample.
   * @param start Starting time of the sample (in nanoseconds since epoch)
   * @param dur The duration of the sample (in nanoseconds).
   */
  public TimeSample(long start, float dur) {
    myDur = dur;
    myStart = start;
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Public Access">
  
  /**
   * Set the starting time of the measurement (in nanoseconds).
   */
  public void setStart(long v) { myStart = v; }
  /**
   * Get the starting time of the measurement (in nanoseconds).
   */
  public long getStart() { return myStart; }
  
  /**
   * Set the duration of the measurement (in nanoseconds).
   */
  public void setDuration(float v) { myDur = v; }
  
  /**
   * Get the duration of the measurement (in nanoseconds).
   */
  public float getDuration() { return myDur ; }
  
  // </editor-fold>

  }

