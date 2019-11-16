package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.collect.MinMaxPriorityQueue;
/**
 * Implementation of Fagin's Threshold Algorithm.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class FaginsAlgorithm {

  List<PriorityQueue<NodeEntry>> prqs;
  int numAnswers;
  List<Map<Integer, PathDistance>> shortestPaths;
  
  public FaginsAlgorithm(List<PriorityQueue<NodeEntry>> prqs, int numAnswers, List<Map<Integer, PathDistance>> shortestPaths){
    this.prqs = prqs;
    this.numAnswers = numAnswers;
    this.shortestPaths = shortestPaths;
  }

  /**
   * Fetches top-k nodes.
   * @return Top-k answers.
   */
  public MinMaxPriorityQueue<NodeEntry> getTopKNodes() {
    int index=0;
    MinMaxPriorityQueue<NodeEntry> mmpq = MinMaxPriorityQueue.orderedBy(Comparator.comparing(NodeEntry::getDistance))
        .maximumSize(numAnswers)
        .create(); 
    List<Integer> topVals = new ArrayList<Integer>();
    
    for(int c=0;c<this.prqs.size();c++)
      topVals.add(prqs.get(c).peek().distanceFromSrc);
    while(prqs.get(0).size()>0) {
      PriorityQueue<NodeEntry> prq = prqs.get(index);
      NodeEntry ne = prq.poll();
      int node = ne.nodeID;
      int totalDistance = ne.distanceFromSrc;
      topVals.remove(index);
      topVals.add(index,ne.distanceFromSrc);
      for(int j=0;j<this.prqs.size();j++) {
        if(j==index)
          continue;
        int keywordNodeDistance = this.shortestPaths.get(j).get(node).getDistance();
        totalDistance+=keywordNodeDistance;
        NodeEntry toBeRemoved = new NodeEntry(node, keywordNodeDistance);
        this.prqs.get(j).remove(toBeRemoved);
      }
      mmpq.add(new NodeEntry(node,totalDistance));
      index=(index+1)%prqs.size();
      int lowestValue = mmpq.peekLast().distanceFromSrc;
      int threshold = 0;
      for(int c=0;c<this.prqs.size();c++)
        threshold+=topVals.get(c);
      if(threshold>lowestValue) {
        break;
      }
    }
    return mmpq; // Returns answers in exact order.
  }
}
