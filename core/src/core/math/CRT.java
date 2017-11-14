package core.math;

/**
 * Provides a computation of the Chinese remainder theorem.
 * @author Shannon
 */
public class CRT {
    
  // <editor-fold desc="Main Computation">

  /**
   * Compute the CRT provided the moduli and remainders.
   * @return The value of (x).
   */
  public static long CRT(long[] moduli, long[] remainders) {
    long M = 0;
    for(int i=0;i<moduli.length;i++) M += moduli[i];
    
    long x = 0;
    for(int i=0;i<moduli.length;i++) {
      long m = moduli[i];
      long a = remainders[i];
      x += ((a* Math.pow(M/m,m-2)*(M/m)) % M);
      }
    
    return x;
    }
  // </editor-fold>
  
  // <editor-fold desc="Main">
  public static void main(String[] args) {
    
    long[] a = {15,2,8,4,14};
    long[] m = {2,3,5,7,11};
    System.out.println(CRT(m,a));
    }
  
  // </editor-fold>
  
  }
