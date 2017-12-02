/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.math;

import core.data.TimeSample;

/**
 * Provides a utility with various number theoretic
 * functions and utilities.
 */
public abstract class NumberTheory {
  
  // <editor-fold desc="Congruence">
  
  /// <summary>
  /// Find the inverse of the given value (mod m).
  /// 
  /// Note: val and m must be relative prime.
  /// 
  /// </summary>
  /// <param name="x">The value to find the inverse (mod m)</param>
  /// <param name="m">The modulus</param>
  /// <returns>The inverse of val (mod m)</returns>
  public static long inverse(long x, long m) {
    
    if(x == 1) return 1;
    
    // Fermat's little theorem.
    if(Primes.isPrime(m) && x != m)
      return (long)Math.pow(x%m,m-2) % m;
    
    long t = 1;
    for(;t<=m;t++) {
      if(((x*t) % m) == 1)
        return t % m;
      }
    
    if(t > Integer.MAX_VALUE)
      throw new IllegalArgumentException("The specified values (" + x + "," + m + ") inverse are out of supported range!");
    else
      throw new IllegalArgumentException("The specified values (" + x + "," + m + ") are not relatively prime.");
    }
  
  
  /**
   * Compute the Chinese Remainder Theorem.
   * 
   *  The CRT:
   *  
   *  x = c1 (mod m1)
   *  x = c2 (mod m2)
   *  ...
   *  x = cn (mod mn)
   *  
   *  where:   (m1,m2,...mn) = 1
   *  
   * @param m The set of moduli each corresponding to its associated remainder in remainders
   * @param c The remainders for each linear congruence
   * @param cnt The number of items to use.
   * @param s The starting index to use.
   * @return The value of x
   */ 
  public static long CRT(TimeSample[] c, TimeSample[] m, int s, int cnt) {
    long[] cc = new long[cnt];
    long[] mm = new long[cnt];
    
    for(int i=0;i<cc.length;i++) {
      cc[i] = (long)c[s + i].getDuration();
      mm[i] = (long)m[s + i].getDuration();
      }
    
    return CRT(cc,mm);
    }
  
  /**
   * Compute the Chinese Remainder Theorem.
   * 
   *  The CRT:
   *  
   *  x = c1 (mod m1)
   *  x = c2 (mod m2)
   *  ...
   *  x = cn (mod mn)
   *  
   *  where:   (m1,m2,...mn) = 1
   *  
   * @param m The set of moduli each corresponding to its associated remainder in remainders
   * @param c The remainders for each linear congruence
   * @return The value of x
   */ 
  public static long CRT(long[] c, long[] m) {
    long X = 0;
    long M = 1;
    for(int i=0;i<m.length;i++) M *= m[i];
    for(int i=0;i<c.length;i++) X += (c[i]*inverse(M/m[i],m[i])*(M/m[i])) % M;
    return X % M;
    }
// </editor-fold>
  
  public static void main(String[] args) {
    
    }
  }
