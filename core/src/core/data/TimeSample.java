package core.data;

/**
 * This is the main class that holds information about a sample
 * of processing time or idle time.
 * 
 * @author Shannon
 */
public class TimeSample {

  // <editor-fold desc="Enums">
  public enum eSource {
    /**
     * This sample came from a thread.
     */
    Thread,
    /**
     * This sample came from a CPU core.
     */
    Core,
    }
  // </editor-fold>
  
  // <editor-fold desc="Private Members">
  private long myStart;
  private float myDur;
  private eSource mySource;
  private int myCID;
  // </editor-fold>
  
  // <editor-fold desc="Constructors">
  
  /**
   * Copy constructor.
   */
  public TimeSample(TimeSample other) {
    myCID = other.myCID;
    myDur = other.myDur;
    mySource = other.mySource;
    myStart = other.myStart;
    } 
  
  /**
   * Create a sample.
   * @param start Starting time of the sample (in milliseconds since epoch)
   * @param dur The duration of the sample (in milliseconds).
   * @param tid  The thread or core ID that this sample came from.
   */
  public TimeSample(eSource source, long start, float dur, int tid) {
    mySource = source;
    myDur = dur;
    myStart = start;
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Public Access">
  /**
   * Get the source of this sample.
   */
  public eSource getSource() { return mySource; }
  
  /**
   * Set the source of this time sample.
   */
  public void setSource(eSource s) { mySource = s; }
  
  /**
   * Set the starting time of the measurement (in milliseconds).
   */
  public void setStart(long v) { myStart = v; }
  
  /**
   * Get the starting time of the measurement (in milliseconds).
   */
  public long getStart() { return myStart; }
  
  /**
   * Set the duration of the measurement (in milliseconds).
   */
  public void setDuration(float v) { myDur = v; }
  
  /**
   * Get the duration of the measurement (in milliseconds).
   */
  public float getDuration() { return myDur ; }
  
  /**
   * Identifies which thread or CORE this came from.
   */
  public int getTID() { return myCID; }
  
  /**
   * Set which thread or CORE this came from.
   */
  public void setTID(int id){ myCID = id; }
  // </editor-fold>

  @Override
  public String toString() {
    return "ID: " + getTID() + " T: " + getStart() + " D:" + getDuration() + " ns";
    }
  }

