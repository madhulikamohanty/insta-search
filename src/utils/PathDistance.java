package utils;

import java.util.List;
/**
 * Shortest paths and distance for a node.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class PathDistance implements Comparable<PathDistance>{
  public List<Integer> path;
  public Integer distance;
  
  public PathDistance(List<Integer> path, Integer distance){
    this.path = path;
    this.distance = distance;
  }
  
  public List<Integer> getPath(){
    return this.path;
  }

  public int getDistance(){
    return this.distance;
  }

  @Override
  public int compareTo(PathDistance other) {
    return Integer.compare(this.distance, other.distance);
  }
}
