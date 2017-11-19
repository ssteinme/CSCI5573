/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.io;

/**
 * This is a very simple logging utility.
 * @author Shannon
 */
public class Log {
  
  public static void error(Exception ex) {
    System.out.println(ex.getMessage());
    ex.printStackTrace();
    }
  
  public static void error(String msg) { 
    System.out.println("ERROR: " + msg);
    }
  
  public static void warn(String msg) { 
    System.out.println("WARNING: " + msg);
    }
  
  public static void info(String msg) { 
    System.out.println("INFO: " + msg);
    }
  
  }
