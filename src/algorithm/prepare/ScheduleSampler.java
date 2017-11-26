package algorithm.prepare;

import core.data.TimeSample;
import core.data.TimeSample.eSource;
import core.math.Conversions;
import core.math.ExtraMath;
import core.io.Log;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import algorithm.tuning.SystemTuning;
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
    
  // Every element of this array will ALWAYS be non-null
  // Elements should only be ignored if the start time is zero.
  private Hashtable<Long,Marker[]> myMarkerMap = new Hashtable<>();
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
    if(stats == null) myCPUStats.put(cid, stats = new Stats(eSource.Core,cid));
    return stats;
    }
  
  /**
   * Given the CPU ID get the time markers for that CPU.
   * (This should always be called in a synchorinzed block)
   * @param id A thread or CPU id.
   */
  private Marker[] getTimeMarkers(long id) {
    Marker[] m = myMarkerMap.get(id);
    if(m == null) {
      myMarkerMap.put(id,m = new Marker[SystemTuning.MAX_THREADS_PER_CPU]);
      for(int i=0;i<m.length;i++) m[i] = new Marker();
      }
    return m;
    }
  
  /**
   * Call this when the timing action is complete or should be updated.
   * 
   * @param t The marker ID received from a call to {@link #mark()}.
   * @param cid The thread or CPU id expiring the timer.
   * @param extend If true then the timer is not expired, but the duration is extended
   * to now.
   */
  private void doExpire(long cid, int t, boolean extend) {
    long time = System.currentTimeMillis();
    Marker ts;
    TimeSample msg = null;
    
    synchronized(myMutex) {
      
      if(t == -1)
        return;
      
      Marker[] myMarkers = getTimeMarkers(cid);
      
      ts = myMarkers[t];
      
      double dur = time - ts.getDuration();
      
      msg = new TimeSample(ts);
      msg.setDuration(dur);
      
      if(!extend)
        ts.setDuration(dur);
      
      switch(ts.getSource()) {
        case Core:
          myAllCPUStatus.add(dur);
          getCPUStats(cid).add(dur);
          break;
        case Thread:
          myAllThreadStats.add(dur);
          break;
          }
       
      if(!extend)
        ts.setInUse(false);
      }
    
    fireSampleAdded(msg);
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
    expire(cid,mark(cid,eSource.Core));
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
      Marker[] markers = getTimeMarkers(cid);
      long time = System.currentTimeMillis();
      int i = 0;
      Marker ts = null;
      
      for(i=0;i<markers.length;i++)
        if(!markers[i].isInUse()) {
          ts = markers[i];
          break;
          }
      
      if(ts == null)
        return -1;
      
      ts.setInUse(true);
      ts.setTID(cid);
      ts.setSource(source);
      ts.setStart(time - myEpoch);
      ts.setDuration(time);
      
      return i;
      }
    }
  
  /**
   * Call this when the timing an action, but instead of being done you
   * just want to make note that the process is still ongoing.
   * @param t The marker ID received from a call to {@link #mark()}.
   * @param cid The thread or CPU id expiring the timer.
   */
  public void extend(long cid, int t) {
    doExpire(cid, t, true);
    }

  /**
   * Call this when the timing action is complete.
   * @param t The marker ID received from a call to {@link #mark()}.
   * @param cid The thread or CPU id expiring the timer.
   */
  public void expire(long cid, int t) {
    doExpire(cid, t,false);
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Properties">
  
  /**
   * Capture the current set of time samples.
   * @param The type of samples to retrieve.
   */
  public TimeSample[] getSamples(eSource src) {
    
    synchronized(myMutex) {
      List<TimeSample> samps = new ArrayList<>();
      
      for(Object k : myMarkerMap.keySet()) {
        Marker[] markers = myMarkerMap.get(k);
        
        for(int i=0;i<markers.length;i++) {
          if(!markers[i].isInUse() && markers[i].getStart() != 0 && markers[i].getSource() == src)
            samps.add(markers[i]);
          }
        }
      
      return samps.toArray(new TimeSample[samps.size()]);
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
  
  // <editor-fold desc="Events">
  
  /**
   * Add a listener to be notified of new times.
   */
  public void addListener(SampleListener sl) {
    synchronized(ourMutex) {
      if(!myListeners.contains(sl))
        myListeners.add(sl);
      }
    }
  
  /**
   * Remove a listener from being notified of new times.
   */
  public void removeListener(SampleListener sl) {
    synchronized(ourMutex) {
      myListeners.remove(sl);
      }
    }
    
  /**
   * Manually force the notification of a new time sample.
   */
  public void fireSampleAdded(TimeSample ts) {
    synchronized(myListeners) {
      for(int i=0;i<myListeners.size();i++) {
        try {
          myListeners.get(i).sampleAdded(ts);
          }
        catch(Exception ex) {
          Log.error(ex);
          }
        
        }
      }
    }
  // </editor-fold>
  
  // <editor-fold desc="Internal Class">
   
  /**
   * This is an internal class used to keep track of 
   * some additional information about a TimeSample.
   */
  class Marker extends TimeSample {
  
    // <editor-fold desc="Private Members">
    private boolean myInUse = false;
    // </editor-fold>
    
    public Marker() { super(eSource.Core,0,0,-1); }
    public boolean isInUse() { return myInUse; }
    public void setInUse(boolean v) { myInUse = v; }
    
    @Override
    public String toString() {
      return super.toString() + " U: " + isInUse();
      }
    }
  
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
      if(myID != -1)
        return ((mySRC == eSource.Core)?"CPU ":"THR") + myID + " Ave Idle (ms): " + myAve + " (ms)";
      else
        return "Ave Idle (ms): " + myAve + " (ms)";
      }
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Reporting">
  
  /**
   * Returns a report string that shows all current metrics.
   */
  public String getReport() {
    String msg = "CPU " + myAllCPUStatus.toString() + " Thread " + myAllThreadStats.toString() + "\n";
    for(Stats s : myCPUStats.values()) msg += "----" + s.toString() + "\n";
    return msg;
    }
  
  @Override
  public String toString() { return getReport(); }
  
  // </editor-fold>

  public static void main(String[] args) {
        
    }
  }
