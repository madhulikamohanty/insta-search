package paths;

import utils.Config;

/**
 * Class to run 'k' hop neighborhood computation in multiple threads.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class MultiThreadPathComputer {

  public static void main(String[] args) {
    System.out.println("Processing "+KNeighbourhoodComputer.nodes.size()+" nodes for the graph.");
    
    int startIndex=0, endIndex;
    int step = KNeighbourhoodComputer.nodes.size()/(Config.numThreads-1)-1;
    
    System.out.println("Initializing threads now.....");
    
    for (int i=0; i<(Config.numThreads-1); i++) 
    { 
      endIndex = startIndex+step;
      Thread thread = new Thread(new KNeighbourhoodComputer(startIndex,endIndex)); 
      thread.start(); 
      startIndex = endIndex+1;
    } 
    
    // Last thread.
    Thread thread = new Thread(new KNeighbourhoodComputer(startIndex,KNeighbourhoodComputer.nodes.size()-1));
    thread.start();
    
    System.out.println("All threads up and running now!!");
  }

}
