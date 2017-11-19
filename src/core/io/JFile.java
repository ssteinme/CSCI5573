package core.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * A file utility.
 * @author Shannon
 */
public class JFile extends File {
  
  // <editor-fold desc="Constructors">
  public JFile(String path) { super(path); }
  // </editor-fold>
  
  /**
   * Set the text of this file.
   */
  public void setText(String txt) {
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(this));
      pw.write(txt);
      pw.close();
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    } 

  /**
   * Append text to the file.
   */
  public void appendText(String txt) {
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(this,true));
      pw.write(txt);
      pw.close();
      }
    catch (Exception ex) {
      Log.error(ex);
      }
    }
  
  /**
   * Get all text from the file.
   */
  public String getText() {
    try {
      BufferedReader br = new BufferedReader(new FileReader(this));
      char[] buf = new char[(int)length()];
      br.read(buf);
      br.close();
      return new String(buf);
      } 
    catch (Exception ex) {
      Log.error(ex);
      }
    
    return null;
    }
  }
