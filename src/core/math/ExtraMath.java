package core.math;

/**
 * Utility class with various calculations.
 * @author Shannon
 */
public class ExtraMath {
  
  /**
   * Provided a value this returns a moving average.
   * 
   * @param curAve The current average.
   * @param x The new input value.
   * @param N The current count (sample count) of the given x.
   * @return The moving average value.
   */
  public static float mave(float x, float curAve, float N) {
    if(N == 0) throw new IllegalArgumentException("Remember this is a 1 based count!");
    return ((curAve*(N-1)) + x)/N;
    }
  
  public static void main(String[] args) {
    float[] x = {11,5,3,1,3,2,5,7,8};
    float a = 0;
    
    for(int i=0;i<x.length;i++)
      System.out.println(a = mave(x[i],a,(int)(i+1)));
    }
  
  }
