package algorithm.prepare;

import core.data.TimeSample;
import core.data.TimeSample.eSource;
import core.math.ExtraMath;
import java.util.ArrayList;
import java.util.List;

import algorithm.tuning.SystemTuning;
import core.math.Conversions;
import java.time.Duration;
import java.util.Hashtable;

/**
 * This class provides the utility that watches the processing
 * and samples the schedules as they do their work.
 * 
 * The class provides a simple utility that plugs into a processing environment
 * and does all the work.  This automatically sends it's information to the
 * SchedulerGuesser singleton.
 * 
 * Example of How To Use This Class:
 *
 * void doSomeProcessing() {
 *   long t = SchedulerSampler.instance().mark()
 *   ...  // The processing or idle time happens here.
 *   SchedulerSampler.instance().expire(t);
 *   }
 * 
 * @author Shannon S.
 */
public class ScheduleSampler {
  
  // <editor-fold desc="Singleton Members">
  private static Object ourMutex = new Object();
  private static ScheduleSampler ourInst = null;
  // </editor-fold>
  
  // <editor-fold desc="Private">
  
  // This is a moving list.
  private Object myMutex = new Object();
    
  // This map is the Key to the marker which keeps track
  // of a current time being taken by a process.
  private Hashtable<Integer,TimeSample> myMarkerMap = new Hashtable<>();
  
  // This has the most recent list of times captured by processing.
  private List<SnapStats> mySnapshot = new ArrayList<>();
  
  private Hashtable<Long,Stats> myCPUStats = new Hashtable<>();
  private int myNextIndex = -1;
  
  // CPU average.
  private Stats myAllCPUStatus = new Stats(eSource.Core, -1);
  
  // Thread average.
  private Stats myAllThreadStats = new Stats(eSource.Thread,-1);
    
  private long myEpoch = System.currentTimeMillis();
  
//  // The time of the very first item sampled
//  // is placed into this value.
//  // all subsequent schedule times are relative to this.
//  // This keeps the values small.
//  private long myEpoch = 0;
  
  private List<SampleListener> myListeners = new ArrayList<>();
  
  // </editor-fold>
  
  // <editor-fold desc="Private Util">
  
  /**
   * Get the statistics for a CPU.
   * @param cid a CPU id.
   */
  private Stats getCPUStats(long cid) {
    Stats stats = myCPUStats.get(ourMutex);
    if(stats == null) 
      myCPUStats.put(cid, stats = new Stats(eSource.Core,cid));
    return stats;
    }
  
  /**
   * Call this when the timing action is complete or should be updated.
   * 
   * @param t The marker ID received from a call to {@link #mark()}.
   * @param extend If true then the timer is not expired, but the duration is extended
   * to now.
   */
  private void doExpire(int t, boolean extend) {
    long time = System.currentTimeMillis();
    long nt = System.nanoTime();
    TimeSample ts;
    
    synchronized(myMutex) {
      
      if(t == -1)
        return;
      
      ts = myMarkerMap.get(t);
      if(ts == null) return;
      
      double dur = time - ts.getDuration();
      
      if(dur == 0)
        dur = (((double)nt) - ts.getDurationNS())*Conversions.NS_TO_MS;
      
      if(!extend) {
        ts.setDuration(dur);
        
        if(ts.getSource() != eSource.Core)
          myMarkerMap.remove(t);
        }
      
      // Find corresponding item in the snapshot if there.
      SnapStats snap = null;
      for(int i=0;i<mySnapshot.size();i++) {
        SnapStats tss = mySnapshot.get(i);
        if(tss.getTID() == ts.getTID() && tss.getSource() == ts.getSource()) {
          snap = tss;
          break;
          }
        }
      
      // Add to snapshot list if not there already.
      if(snap == null) {
        snap = new SnapStats(ts);
        mySnapshot.add(snap);
        }
      
      // Update snapshot data.
      snap.DURATION_STATS.add(dur);
      snap.setDuration(snap.DURATION_STATS.myAve);
      
      if(mySnapshot.size() >= SystemTuning.MAX_THREADS_PER_CPU && ts.getSource() == eSource.Thread)
        mySnapshot.remove(0);
      
      switch(ts.getSource()) {
        case Core:
          myAllCPUStatus.add(dur);
          getCPUStats(ts.getTID()).add(dur);
          break;
        case Thread:
          myAllThreadStats.add(dur);
          break;
          }
      }
    }
     
  // </editor-fold>

  // <editor-fold desc="Private Constructors">
  
  /**
   * Singleton constructor.
   */
  private ScheduleSampler() {
    
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Time sampling">
  
  /**
   * Notify the sampler of the specified CPU id.
   */
  public void notify(long cid) {
    expire(mark(cid,eSource.Core));
    }
  
  /**
   * Start a marker that begins counting the processing time.
   * This is called at the start of processing or idle.
   * This works with the {@link #expire(long)} method.
   * 
   * @param cid The unique ID for the thread/CPU.
   * @param source Who is starting this sample.
   * @return A value that acts like a key to be used when this
   * time sampling is done. 
   */
  public int mark(long cid, eSource source) {
    
    synchronized(myMutex) {
      long time = System.currentTimeMillis();
      long nt = System.nanoTime();
      
      if(source == eSource.Thread && myMarkerMap.size() >= SystemTuning.MAX_THREADS_PER_CPU)
        return -1;
      
      String id = "" + source + "" + cid;
      
      TimeSample ts = new TimeSample();
      ts.setTID(cid);
      ts.setSource(source);
      ts.setStart(time - myEpoch);
      ts.setDuration(time);
      ts.setDurationNS(nt);
      myMarkerMap.put(id.hashCode(),ts);
      
      return id.hashCode();
      }
    }
  
  /**
   * Call this when the timing an action, but instead of being done you
   * just want to make note that the process is still ongoing.
   * @param t The marker ID received from a call to {@link #mark()}.
   * @param cid The thread or CPU id expiring the timer.
   */
  public void extend(int t) {
    doExpire(t, true);
    }

  /**
   * Call this when the timing action is complete.
   * @param t The marker ID received from a call to {@link #mark()}.
   * @param cid The thread or CPU id expiring the timer.
   */
  public void expire(int t) {
    doExpire(t,false);
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Properties">
  
  /**
   * Get the current set of time samples.
   */
  public TimeSample[] getSamples(eSource src) {
    
    synchronized(myMutex) {
      List<TimeSample> rret = new ArrayList<>();
      
      for(int i=0;i<mySnapshot.size();i++) {
        TimeSample ts = mySnapshot.get(i);
        if(ts.getSource() == src)
          rret.add(new TimeSample(ts));
        }
      
      return rret.toArray(new TimeSample[rret.size()]);
      }
    
    }
  
  // </editor-fold>

  // <editor-fold desc="Singleton Access">

  /**
   * Get the instance to this class for use.
   */
  public static ScheduleSampler instance() {
    synchronized(ourMutex) {
      if(ourInst == null) ourInst = new ScheduleSampler();
      return ourInst;
      }
    }
  // </editor-fold>
  
  // <editor-fold desc="Internal Class">
  
  /**
   * Stats on the CPU.
   */
  class Stats {
    private long myN = 0;           // Sample count for this CPU
    private double myAve = 0;   // Current moving average idle value for this CPU.
    private long myID;
    private eSource mySRC;
    
    public Stats(eSource src, long id) {
      myID = id;
      mySRC = src;
      }
    
    /**
     * Add a new sample value to the stats.
     */
    public void add(double x) {
      
      // Compute 
      // Reset moving average if we exceed counts.
      if( (myN+1) >= Integer.MAX_VALUE) {
        myN = 0;
        myAve = 0;
        }
      
      myN++;
      myAve = ExtraMath.mave(x,myAve,myN);
      }
    
    public String toString() {
      String id = (myID == -1)?"":"(" + myID + ")";
      String tm = (myAve < 1)?myAve*1000 + "(ns)":myAve + " (ms)";
      return ((mySRC == eSource.Core)?"CPU " + id + " Ave Idle ":"THR Ave Use ") + tm;
      }
    }
  
  /**
   * Used to hold a moving average of time samples for threads/CPUs.
   */
  class SnapStats extends TimeSample {
    public Stats DURATION_STATS;
    
    public SnapStats(TimeSample ts) {
      super(ts);
      DURATION_STATS = new Stats(ts.getSource(), ts.getTID());
      }
    
    public SnapStats(eSource src) { DURATION_STATS = new Stats(src,-1); }
    
    @Override
    public void setTID(long id) {
      super.setTID(id);
      DURATION_STATS.myID = id;
      }
    
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Reporting">
  
  /**
   * Returns a report string that shows all current metrics.
   */
  public String getReport() {
    String msg = myAllCPUStatus.toString() + " " + myAllThreadStats.toString() + "\n";
    for(Stats s : myCPUStats.values()) msg += "  >> " + s.toString() + "\n";
    return msg;
    }
  
  @Override
  public String toString() { return getReport(); }
  
  // </editor-fold>

  public static void main(String[] args) {
        
    }
  }
