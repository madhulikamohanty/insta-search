package utils;

import java.util.Comparator;

public class NodeEntryComparator implements Comparator<NodeEntry>{ 

  // Overriding compare() method of Comparator  
  // for ascending order of distance 
  public int compare(NodeEntry n1, NodeEntry n2) { 
    if (n1.distanceFromSrc < n2.distanceFromSrc) 
      return -1; 
    else if (n1.distanceFromSrc > n2.distanceFromSrc) 
      return 1; 
    return 0; 
  } 
}