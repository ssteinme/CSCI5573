package core.math;

/**
 * Utility class with various calculations.
 * @author Shannon
 */
public class ExtraMath {
  
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
  
  public static void main(String[] args) {
    double[] x = {11,5,3,1,3,2,5,7,8};
    double a = 0;
    
    for(int i=0;i<x.length;i++)
      System.out.println(a = mave(x[i],a,(int)(i+1)));
    }
  
  }
