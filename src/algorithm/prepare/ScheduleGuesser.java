package algorithm.prepare;

import algorithm.tuning.PerformanceTiming;
import algorithm.tuning.SystemTuning;
import core.data.Schedule;
import core.data.TimeSample;
import core.io.JFile;
import core.math.NumberTheory;
import core.math.Primes;
import core.io.Log;
import core.math.Conversions;

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
    PerformanceTiming.CRT_GUESS_TIME = System.nanoTime();
    TimeSample[] samples = ScheduleSampler.instance().getSamples();
    
    long[] remain =  new long[samples.length];
    long[] primes = new long[samples.length];
    for(int i=0;i<samples.length;i++) {
      primes[i] = (long)samples[i].getDuration();
      if(primes[i] == 0) primes[i] = 1;
      remain[i] = primes[i];
      }
    
    // Move everything to closest prime number.
    Primes.makeClosestPrime(primes);
    long x = NumberTheory.CRT(remain,primes);
    
    // Compute the new time slot for each process.
    for(int i=0;i<samples.length;i++) {
      long s = Math.abs((x*primes[i]) % remain[i]);
      samples[i].setStart(s);
      }
    
    mySchedule = new Schedule(samples);
    PerformanceTiming.CRT_GUESS_TIME = (System.nanoTime() - PerformanceTiming.CRT_GUESS_TIME)*Conversions.NS_TO_MS;
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
  
  /**
   * Testing method that tests the behavior of making a first schedule guess.
   */
  public static void testScheduleGuess() {
    
    try {
      
      for(int i=0;i<100;i++) {
        int t = ScheduleSampler.instance().mark(TimeSample.eSource.Core);
        int r = (int)(Math.random()*200);
        Thread.sleep(r);
        ScheduleSampler.instance().expire(t);
        }
      
      Schedule sch = ScheduleGuesser.instance().getSchedule();
      System.out.println(sch.toCSVString());
      
      new JFile("C:\\Users\\Shannon\\Desktop\\test.csv").setText(sch.toCSVString());
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    
    }
  
    public static void main(String[] args) {
      testScheduleGuess();
      }
  
  }
