package utils;
/**
 * Class to print debug statements when debug mode is true.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class Debug {
  static boolean debugMode = false;
  
  public static void println(String string) {
    if(debugMode)
      System.out.println(string);
  }

}
