/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.math;

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
      throw new IllegalArgumentException("The specified values (" + x + "," + m + ") are not relatively prime, or are too large.");
    }
  
  /// <summary>
  /// Compute the Chinese Remainder Theorem.
  /// 
  /// The CRT:
  /// 
  /// x = c1 (mod m1)
  /// x = c2 (mod m2)
  /// ...
  /// x = cn (mod mn)
  /// 
  /// where:   (m1,m2,...mn) = 1
  /// 
  /// </summary>
  /// <param name="m">The set of moduli each corresponding to its associated remainder in remainders</param>
  /// <param name="c">The remainders for each linear congruence</param>
  /// <returns>The value of x</returns>
  public static long CRT(int[] c, int[] m) {
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
