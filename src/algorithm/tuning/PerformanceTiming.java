package algorithm.tuning;

/**
 * This class provides a holding location for all performance
 * times that occur during processing.
 * 
 * @author Shannon
 */
public class PerformanceTiming {
  
  /**
   * Total time it takes for the CRT algorithm to run.
   * Always in milliseconds.
   */
  public static double CRT_TIME = 0;
    
  /**
   * Has the most recent account of the time the
   * initial schedule guess takes.
   * Time is always in milliseconds.
   */
  public static double ALGORITHM_GUESS_TIME = 0;
  
  /**
   * Has the most recent time for the graph optimization algorithm.
   * Always in milliseconds.
   */
  public static double GRAPH_TIME = 0;
  
  }

