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
  private int myNextIndex = -1;
  
  // CPU average.
  private double myCPUA = 0;
  private int myCPUN = 0;
  // Thread average.
  private double myThreadA = 0;
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
  
  /**
   * Singleton constructor.
   */
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
   * @param cid The unique ID for the thread/CPU.
   * @param source Who is starting this sample.
   * @return A value that acts like a key to be used when this
   * time sampling is done.
   */
  public int mark(long cid, eSource source) {
    
    synchronized(myMutex) {
      long time = System.currentTimeMillis();
      int i = 0;
      Marker ts = null;
      
      for(i=0;i<myMarkers.length;i++)
        if(!myMarkers[i].isInUse()) {
          ts = myMarkers[i];
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
   * Call this when the timing action is complete.
   * @param t The marker ID received from a call to {@link #mark()}.
   */
  public void expire(int t) {
    long time = System.currentTimeMillis();
    Marker ts;
    TimeSample msg = null;
    
    synchronized(myMutex) {
      
      if(t == -1)
        return;
      
      ts = myMarkers[t];
      ts.setDuration(time - ts.getDuration());
      msg = new TimeSample(ts);
      
      // Reset moving average if we exceed counts.
      if(myCPUN+1 >= Integer.MAX_VALUE) {
        myCPUN = 0;
        myThreadN = 0;
        myCPUA = 0;
        myThreadA = 0;
        }
      
      switch(ts.getSource()) {
        case Core:
          myCPUN++;
          myCPUA = ExtraMath.mave(ts.getDuration(),myCPUA,myCPUN);
          break;
        case Thread:
          myThreadN++;
          myThreadA = ExtraMath.mave(ts.getDuration(),myThreadA,myThreadN);
          break;
          }
      
      ts.setInUse(false);
      }
    
    fireSampleAdded(msg);
    }
    
  // </editor-fold>
  
  // <editor-fold desc="Properties">
  
  /**
   * Get the average CPU idle time in milliseconds.
   */
  public double getCPUIdle() {
    return myCPUA;
    }
  
  /**
   * Get the average thread use time in milliseconds.
   */
  public double getThreadUse() {
    return myThreadA;
    }
  
  /**
   * Capture the current set of time samples.
   * @param The type of samples to retrieve.
   */
  public TimeSample[] getSamples(eSource src) {
    
    synchronized(myMutex) {
      List<TimeSample> samps = new ArrayList<>();
      for(int i=0;i<myMarkers.length;i++) {
        if(!myMarkers[i].isInUse() && myMarkers[i].getStart() != 0 && myMarkers[i].getSource() == src)
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
  
  // <editor-fold desc="Reporting">
  
  /**
   * Returns a report string that shows all current metrics.
   */
  public String getReport() {
    String msg = " CPU Ave Idle (ms): " + getCPUIdle() + " Thread Ave Use (ms): " + getThreadUse() + "\n";
    return msg;
    }
  
  @Override
  public String toString() { return getReport(); }
  
  // </editor-fold>

  public static void test() {
    
    try {
      for(int i=0;i<10;i++) {
        
        int id = ScheduleSampler.instance().mark(i, eSource.Thread);
        long nxt = (long)(System.currentTimeMillis() + Math.random()*100.0d);
        while(System.currentTimeMillis() < nxt);
        ScheduleSampler.instance().expire(id);
        
        id = ScheduleSampler.instance().mark(100 + i, eSource.Core);
        nxt = (long)(System.currentTimeMillis() + Math.random()*100.0d);
        while(System.currentTimeMillis() < nxt);
        ScheduleSampler.instance().expire(id);
        }

      System.out.println(ScheduleSampler.instance(). getReport());
      
      } 
    catch (Exception ex) {
      Log.error(ex);
      }
    }
  
  public static void main(String[] args) {
    test();
    
    }
  
  }
