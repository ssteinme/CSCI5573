package core.math;

import core.data.TimeSample;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class with various calculations.
 * @author Shannon
 */
public class ExtraMath {
  
  public static long[] copy(long[] l) {
    long[] v = new long[l.length];
    System.arraycopy(l,0,v,0,l.length);
    return v;
    } 
  
  /**
   * Extract a list of all the durations of the specified times.
   */
  public static long[] getDuration(TimeSample[] ts, int cnt) {
    if(cnt > ts.length) throw new IllegalArgumentException("The count asked for " + cnt + " is greater than the total array length " + ts.length);
    long[] l = new long[cnt];
    for(int i=0;i<cnt;i++) 
      l[i] = (long)ts[i].getDuration();
    return l;
    }
  
  /**
   * Get the durations from the time samples de-duplicated.
   */
  public static TimeSample[] deDuplicate(TimeSample[] ts) {
    
    Arrays.sort(ts, new Comparator<TimeSample>() {
      public int compare(TimeSample a, TimeSample b) {
        if(a.getDuration() < b.getDuration())
          return -1;
        else if(a.getDuration() > b.getDuration())
          return 1;
        return 0;
        }
      });
    
    List<TimeSample> aan = new ArrayList<>();
    for(int i=1;i<ts.length;i++) {
      if(ts[i-1].getDuration() != ts[i].getDuration()) 
        aan.add(ts[i]);
      }
    
    return aan.toArray(new TimeSample[aan.size()]);
    }
  
  /**
   * Provided a value this returns a moving average.
   * 
   * @param prevAve The current average.
   * @param x The new input value.
   * @param n The current count (sample count) of the given x.
   * @return The moving average value.
   */
  public static double mave(double x, double prevAve, long n) {
    if(n == 0) throw new IllegalArgumentException("Remember this is a 1 based count!");
    return ((prevAve*( ((double)n)-1)) + x)/((double)n);
    }
  
  /**
   * Take the log base b of a.
   */
  public static double log(double b, double a) {
    return Math.log(a)/Math.log(b);
    }
  
  public static void main(String[] args) {
    double[] x = {11,5,3,1,3,2,5,7,8};
    double a = 0;
    
    for(int i=0;i<x.length;i++)
      System.out.println(a = mave(x[i],a,(int)(i+1)));
    }
  
  }
