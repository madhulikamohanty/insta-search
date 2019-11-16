package clustering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.HierarchicalClusterer;
import com.aliasi.util.Distance;

import languagemodel.EdgeLM;
import languagemodel.NodeLM;
import languagemodel.TreeLM;

/**
 * Clusterer for LM trees.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class Clusterer {

  @SuppressWarnings("unused")
  public static Set<Set<TreeLM>> cluster(Set<TreeLM> trees){
    double maxDistance=2.0D;
    HierarchicalClusterer<TreeLM> clClusterer
    = new CompleteLinkClusterer<TreeLM>(maxDistance,JS_DIVERGENCE);
    Dendrogram<TreeLM> completeLinkDendrogram
    = clClusterer.hierarchicalCluster(trees);

    double CHval=0.0;
    ArrayList<Double> Charr=new ArrayList<Double>();
    int optK=1;
    int maxSize=11; // max #clusters.

    
    for (int kk = 2; kk <= Math.min(maxSize,trees.size()); kk++)
    {
      Set<Set<TreeLM>> clKClusteringtmp = completeLinkDendrogram.partitionK(kk);
      double W= completeLinkDendrogram.withinClusterScatter(kk, JS_DIVERGENCE);
      double T= totalScatter(clKClusteringtmp);
      double B=T-W;
      double CH = B*(trees.size()-kk)/(W*(kk-1));
      Charr.add(CH);
      if(CH>CHval){
        CHval=CH;
        optK=kk;
      }
    }
    Set<Set<TreeLM>> clKClustering = completeLinkDendrogram.partitionK(optK);
    return clKClustering;
  }


  @SuppressWarnings("unused")
  private static double totalScatter(Set<Set<TreeLM>> clKClusteringtmp) {
    double Tval=0;
    Iterator<Set<TreeLM>> treeiter=clKClusteringtmp.iterator();
    ArrayList<TreeLM> allTrees = new ArrayList<TreeLM>();
    while(treeiter.hasNext()){
      allTrees.addAll(treeiter.next());
    }
    for(int i=0;i<allTrees.size();i++){
      TreeLM tI=allTrees.get(i);
      for(int k=0;k<allTrees.size();k++){
        TreeLM tK=allTrees.get(k);
        if(i==k)
          continue; 
        double dVal=0.0;
        dVal=JS_DIVERGENCE.distance(tI, tK);
        Tval=Tval+dVal;
      }
    }
    return Tval/2;
  }

  static final Distance<TreeLM> JS_DIVERGENCE 
  
  = new Distance<TreeLM>() {
    public double distance(TreeLM tree1, TreeLM tree2) {
      NodeLM nlm1 = tree1.getNodeLM();
      NodeLM nlm2 = tree2.getNodeLM();
      double nodeDivergence = nlm1.computeJSD(nlm2);
      
      EdgeLM elm1 = tree1.getEdgeLM();
      EdgeLM elm2 = tree2.getEdgeLM();
      double edgeDivergence = elm1.computeJSD(elm2);
      return 0.5*(nodeDivergence+edgeDivergence); 
    }
  };
}
