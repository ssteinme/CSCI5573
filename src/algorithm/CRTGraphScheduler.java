/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.prepare.SampleListener;
import algorithm.prepare.ScheduleSampler;
import algorithm.prepare.ThreadScheduler;
import algorithm.tuning.PerformanceTiming;
import algorithm.tuning.SystemTuning;
import core.data.Schedule;
import core.data.Schedule.Thread2CPU;
import core.data.TimeSample;
import core.io.Log;
import core.math.NumberTheory;
import core.math.Primes;
import core.math.TimeStamp;
import simulator.CPU;
import core.wdag.Dijkstra;
import core.wdag.Edge;
import core.wdag.Vertex;
import java.util.ArrayList;
import java.util.List;


/**
 * This provides a scheduler algorithm that utilizes a CRT guessing
 * scheme combined with a directed graph shortest path technique that
 * selects optimal schedule.
 * 
 * This class assumes that the {@link SampleListener} class is properly plugged into
 * the framework recording processing times.
 * 
 * @author Shannon S.
 */
public class CRTGraphScheduler extends ThreadScheduler {

  // <editor-fold desc="Private Members">
  private Schedule mySchedule = null;
  // </editor-fold>
  
  // <editor-fold desc="Private Util">
  
  /**
   * Create a graph and optimize the association of thread to CPU.
   * @param sz The number of CPU nodes to create.
   */
  private Thread2CPU[] graphOptimize(TimeSample[] threadDurations, TimeSample[] cpus, int sz) {
    Vertex[] vertices  = new Vertex[sz];
    
    for(int i=0;i<sz;i++) {
      vertices[i] = new Vertex("" + cpus[i].getTID());
      vertices[i].currentMinDist = cpus[i].getDuration();
      }
    
    int td = 0;
    
    // Make a K_n braph
    for(int i=0;i<vertices.length;i++) {
      for(int j=0;j<vertices.length;j++) {
        if(i == j) continue;
        Edge e = new Edge("" + threadDurations[td].getTID(), vertices[j],threadDurations[td].getDuration());
        vertices[i].addEdge(e);
        td = (td + 1) % threadDurations.length;
        }
      }
    
    // Dijkstra.bellmanFord(vertices[0]);
    Dijkstra.computePossiblePaths(vertices[0]);
    
    List<Thread2CPU> tt = new ArrayList<>();
    for(int i=0;i<vertices.length;i++) {
      if(vertices[i].bestEdge != null)
        tt.add(new Thread2CPU(Integer.parseInt(vertices[i].name),Integer.parseInt(vertices[i].bestEdge.name)));
      }
    
    return tt.toArray(new Thread2CPU[tt.size()]);
    }
    
  /**
   * This is the method that generates a schedule.
   */
  private void makeSchedule() {
    
    // <editor-fold desc="Get Raw Time Sample Data">
    TimeStamp time = TimeStamp.mark();
    TimeStamp crtTime = TimeStamp.mark();
    
    TimeSample[] threadSamples = ScheduleSampler.instance().getSamples(TimeSample.eSource.Thread);
    TimeSample[] cpuSamples = ScheduleSampler.instance().getSamples(TimeSample.eSource.Core);
    // </editor-fold>
    
    // <editor-fold desc="If we don't have enough data do Round Robin until we do.">
    if(threadSamples.length < SystemTuning.MAX_THREADS_PER_CPU || cpuSamples.length < getCPUCount()) {
      mySchedule = null;
      PerformanceTiming.ALGORITHM_GUESS_TIME = TimeStamp.expire(time);
      PerformanceTiming.ALGORITHM_GUESS_TIME = TimeStamp.expire(crtTime);
      PerformanceTiming.CRT_TIME = TimeStamp.expire(crtTime);
      return;
      }
    // </editor-fold>
        
    // Spread CPU idle times to prime number distribution.
    int sz = (threadSamples.length > cpuSamples.length)?cpuSamples.length:threadSamples.length;
    Primes.primeDistribute(cpuSamples,0,sz);
    long x = NumberTheory.CRT(threadSamples, cpuSamples, 0, sz);
    long largest = (long)TimeSample.largest(cpuSamples).getDuration();
    for(int i=0;i<sz;i++) cpuSamples[i].setDuration(x*threadSamples[i].getDuration() % largest);
    
    PerformanceTiming.CRT_TIME = TimeStamp.expire(crtTime);
    
    TimeStamp graphTime = TimeStamp.mark();
    Thread2CPU[] sched = graphOptimize(threadSamples,cpuSamples,sz);
    PerformanceTiming.GRAPH_TIME = TimeStamp.expire(graphTime);
    
    // Make the initial schedule
    mySchedule = new Schedule(sched);
    
    
    // Stamp the time.
    PerformanceTiming.ALGORITHM_GUESS_TIME = TimeStamp.expire(time);
    }
  
  // </editor-fold>
  
  // <editor-fold desc=" Constructors">
  
  /**
   * Create the scheduler.
   */
  public CRTGraphScheduler(int nCpus) { super(nCpus); }
  
  /**
   * Force a creation of a new schedule and return it.
   */
  public Schedule getSchedule() {
    makeSchedule();
    return mySchedule;
    }
  
  // </editor-fold>
  
  /**
   * Testing method that tests the behavior of making a first schedule guess.
   */
  public static void testCRTGraphScheduler() {
    
    try {
      double gt = 0;
      
      for(int k=0;k<5;k++) {
        
        for(int i=0;i<25;i++) {
          int t = ScheduleSampler.instance().mark(k, TimeSample.eSource.Core);
          int r = (int)(Math.random()*200);
          Thread.sleep(r);
          ScheduleSampler.instance().expire(t);
          }
        
        Schedule sch = new CRTGraphScheduler(5).getSchedule();
        gt += PerformanceTiming.ALGORITHM_GUESS_TIME;
        System.out.println("----------------------------------------------------\n");
        System.out.println(sch.toCSVString());
        }
      
      System.out.println("Ave: " + (gt/5) + "ms");
      
      //new JFile("C:\\Users\\Shannon\\Desktop\\test.csv").setText(sch.toCSVString());
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    
    }
  
  @Override
  public void run() {
    
    while(!Thread.currentThread().isInterrupted()) {
      
      try {
        
        if(mySchedule == null)
          makeSchedule();
        
        if(mySchedule != null) {
          Thread2CPU next = mySchedule.getNext();
          
          if(next == null) {
            mySchedule = null;
            continue;
            }
          
          CPU cpu = getCPU(next.CPU);
          simulator.Process thread = getThread(next.THREAD);
          
          if(cpu != null && thread != null)
            cpu.run(thread);
          }
        // Round robin until enough data is available to
        // make our schedule.
        else {
          CPU cpu = getIdleCPU();
          simulator.Process p = getReadyThread();
          
          if(cpu != null && p != null)
            cpu.run(p);
          
          Thread.yield();
          }
        
        }
      catch (Exception ex) {
        Log.error(ex);
        }
        
      }
    }
  
  public static void main(String[] args) {
      testCRTGraphScheduler();
      }
  
  }
