package search;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import clustering.Clusterer;
import languagemodel.TreeLM;
import search.utils.MyTreeResult;
import utils.Config;

/**
 * Class to do a Bidirectional Search and cluster the results.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class SearchAndCluster {
  String query;
  int numAnswers;

  public SearchAndCluster(String query, int numAns) {
    this.query = query;
    this.numAnswers = numAns;
  }

  /**
   * Does Bidirectional Keyword Search and clusters the results.
   * @return The number of clusters formed.
   */
  public int doSearchAndCluster() {
    System.out.println("Received search and cluster request..");
    
    BidirSearch ks=new BidirSearch(query);
    List<MyTreeResult> heap=new ArrayList<MyTreeResult>();
    int i=0;
    while(ks.hasNext()){
      MyTreeResult res = ks.next();
      heap.add(res);
      i++;
      if(i==this.numAnswers){
        ks.close();
        break;
      }
    }
    
    System.out.println("Returned "+heap.size()+" answers!");
    
    System.out.println("Building TreeLMs-->");
    Set<TreeLM> treeLMs = buildTreeLMs(heap);
    System.out.println("Done!!");
    System.out.println("Clustering-->");
    Set<Set<TreeLM>> clusters = Clusterer.cluster(treeLMs);
    System.out.println("Done!!");
    List<MyTreeResult> results = new ArrayList<MyTreeResult>(heap);

    // create results directory for cluster files if it does not exist.
    if(!new File(Config.resultsDir).exists()){
      new File(Config.resultsDir).mkdir();
    }

    //Write trees in clusters to JSON files.
    int count = 0;
    for(Set<TreeLM> cluster: clusters){
      count++;
      List<MyTreeResult> thisCluster = new ArrayList<MyTreeResult>();
      //System.out.println("------------------Cluster:"+count+"------------------");
      for(TreeLM tlm:cluster){
        //System.out.println("Tree:"+results.get(tlm.getRank()-1));
        thisCluster.add(results.get(tlm.getRank()-1));
      }
      TreeResultToJSONFile.saveToFile(thisCluster,Config.resultsDir+query.replace(" ", ",")+"-"+count+".json");
    }
    return count;
  }

  private Set<TreeLM> buildTreeLMs(List<MyTreeResult> heap) {
    Set<TreeLM> treeLMs = new TreeSet<TreeLM>();
    Iterator<MyTreeResult> it = heap.iterator();
    int i = 1;
    while(it.hasNext()) {
      MyTreeResult mtr = it.next();
      TreeLM tlm = new TreeLM(mtr,i);
      treeLMs.add(tlm);
      i++;
    }
    return treeLMs;
  }
}
