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
  public static int CRT(int[] moduli, int[] remainders) {
    long M = 1;
    for(int i=0;i<moduli.length;i++) M *= moduli[i];
    
    long x = 0;
    for(int i=0;i<moduli.length;i++) {
      long m = moduli[i];
      long a = remainders[i];
      long mt = M/m;
      long y = NumberTheory.inverse(M/m,m);
      x += (a*y*mt) % M;
      }
    
    return (int)x;
    }
  // </editor-fold>
  
  // <editor-fold desc="Main">
  public static void main(String[] args) {
    int[] a = {15,2,8,4,14,13,17};
    int[] m = { 2,3,5,7,11,23,19};
    
    int ans = CRT(m,a);
    
    // Run a check
    for(int i=0;i<a.length;i++) {
      if(((ans - a[i]) % m[i]) != 0)
        System.out.println("Failed (" + a[i] + "," + m[i] + ")");
      }
    
    }
  
  // </editor-fold>
  
  }
