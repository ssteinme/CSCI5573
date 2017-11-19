package prepare;

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
import tuning.SystemTuning;

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
  private Marker[] myMarkers = new Marker[SystemTuning.MAX_THREADS];
  private int myNextM = -1;
  
  // CPU average.
  private float myCPUA = 0;
  private int myCPUN = 0;
  // Thread average.
  private float myThreadA = 0;
  private int myThreadN = 0;
    
  private long myEpoch = System.currentTimeMillis();
  
//  // The time of the very first item sampled
//  // is placed into this value.
//  // all subsequent schedule times are relative to this.
//  // This keeps the values small.
//  private long myEpoch = 0;
  
  private List<SampleListener> myListeners = new ArrayList<>();
  
  // </editor-fold>
  
  // <editor-fold desc="Private Constructors">
  
  private ScheduleSampler() {
    for(int i=0;i<myMarkers.length;i++) myMarkers[i] = new Marker();
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Time sampling">
    
  /**
   * Start a marker that begins counting the processing time.
   * This is called at the start of processing or idle.
   * This works with the {@link #expire(long)} method.
   * 
   * @param source Who is starting this sample.
   * @return A value that acts like a key to be used when this
   * time sampling is done.
   */
  public int mark(eSource source) {
    
    synchronized(myMutex) {
      long time = System.currentTimeMillis();
      myNextM = (myNextM + 1) % myMarkers.length;
      Marker ts = myMarkers[myNextM];
      
      // If this marker is alreay in use we are full and we 
      // do not take the time snapshot.
      // The user gets -1 which is a NOOP.
      if(ts.isInUse()) return -1;
      
      ts.setTID(myNextM);
      ts.setInUse(true);
      ts.setSource(source);
      ts.setStart(time - myEpoch);
      ts.setDuration(System.nanoTime());
      
      return myNextM;
      }
    }
  
  /**
   * Call this when the timing action is complete.
   * @param t The marker ID received from a call to {@link #mark()}.
   */
  public void expire(int t) {
    Marker ts;
    TimeSample msg = null;
    
    synchronized(myMarkers) {
      
      if(t == -1)
        return;
      
      ts = myMarkers[t];
      ts.setDuration((float)(Math.abs(System.nanoTime() - ts.getDuration())*Conversions.NS_TO_MS));
      msg = new TimeSample(ts);
      ts.setInUse(false);
      }
    
    fireSampleAdded(msg);
    }
    
  // </editor-fold>
  
  // <editor-fold desc="Manually Add">
    
  /**
   * Manually add a sample to the system from a CPU or a thread.
   * 
   * You should try to use the {@link #mark(core.data.TimeSample.eSource)} and {@link #expire(long)} methods 
   * if possible.
   */
  public void add(TimeSample ts) {
    synchronized(myMarkers) {
      switch(ts.getSource()) {
        case Core:
          myCPUA += ExtraMath.mave(ts.getDuration(),myCPUA,myCPUN);
          break;
        case Thread:
          myThreadA += ExtraMath.mave(ts.getDuration(),myThreadA,myThreadN);
          break;
        }
      }
    
    fireSampleAdded(ts);
    }
  
  // </editor-fold>
  
  // <editor-fold desc="Properties">
  
  /**
   * Get the average CPU time use.
   */
  public float getCPUUse() {
    return myCPUA;
    }
  
  /**
   * Get the average thread idle time.
   */
  public float getThreadIdle() {
    return myThreadA;
    }
  
  /**
   * Capture the current set of time samples.
   */
  public TimeSample[] getSamples() {
    
    synchronized(myMutex) {
      List<TimeSample> samps = new ArrayList<>();
      for(int i=0;i<myMarkers.length;i++) {
        if(!myMarkers[i].isInUse() && myMarkers[i].getStart() != 0)
          samps.add(myMarkers[i]);
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
    private boolean myInUse = false;
    public Marker() { super(eSource.Core,0,0,-1); }
    public boolean isInUse() { return myInUse; }
    public void setInUse(boolean v) { myInUse = v; }
    }
  
  // </editor-fold>
  }
