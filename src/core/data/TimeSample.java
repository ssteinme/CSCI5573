package core.data;

import java.util.Arrays;
import java.util.Comparator;

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
  private double myDurNS;
  private double myDurMS;
  private eSource mySource;
  private long myCID;
  // </editor-fold>
  
  // <editor-fold desc="Constructors">
  public TimeSample() {}
  
  /**
   * Copy constructor.
   */
  public TimeSample(TimeSample other) {
    myCID = other.myCID;
    myDurMS = other.myDurMS;
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
    myDurMS = dur;
    myStart = start;
    myCID = tid;
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
   * Set the duration of the measurement (in nanoseconds).
   */
  public void setDurationNS(double v) { myDurNS = v; }
  
  /**
   * Get the duration of the measurement (in nanoseconds).
   */
  public double getDurationNS() { return myDurNS ; }
  
  /**
   * Set the duration of the measurement (in milliseconds).
   */
  public void setDuration(double v) { myDurMS = v; }
  
  /**
   * Get the duration of the measurement (in milliseconds).
   */
  public double getDuration() { return myDurMS ; }
  
  /**
   * Identifies which thread or CORE this came from.
   */
  public long getTID() { return myCID; }
  
  /**
   * Set which thread or CORE this came from.
   */
  public void setTID(long id){ myCID = id; }
  // </editor-fold>
  
  /**
   * Sort the specified timesample array.
   * @param ts Array.
   * @param sidx Starting index to begin sort.
   * @param cnt Count to sort starting from sidx.
   */
  public static void sort(TimeSample[] ts, int sidx, int cnt) {
    
    Arrays.sort(ts, sidx, sidx+cnt, new Comparator<TimeSample>() {
      public int compare(TimeSample a, TimeSample b) {
        if(a.getDuration() < b.getDuration())
          return -1;
        else if(a.getDuration() > b.getDuration())
          return 1;
        return 0;
        }
      }); 
    }
  
  public static void report(TimeSample[] ts) {
    System.out.println("----------- TS ------------");
    for(TimeSample t : ts)
      System.out.println("  >> " + t.toString());
    }
  
  public static TimeSample largest(TimeSample[] ts) {
    TimeSample largest = null;
    
    for(int i=0;i<ts.length;i++) {
      if(largest == null || ts[i].getDuration() > largest.getDuration()) 
        largest = ts[i];
      }
    
    return largest;
    }
  
  @Override
  public String toString() {
    return "ID: " + getTID() + " S: " + getStart() + " D:" + getDuration() + " ms";
    }
  }
