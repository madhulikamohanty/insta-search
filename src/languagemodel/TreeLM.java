package languagemodel;


import languagemodel.NodeLM;
import search.utils.MyTreeResult;
import search.utils.Node;

public class TreeLM implements Comparable<TreeLM>{
  int rank;
  LM nodeLMs;
  LM edgeLMs;

  public TreeLM(MyTreeResult tree, int rank) {
    this.rank = rank;
    computeLMs(tree);
  }

  /**
   * Computes the edge and node LMs for the tree.
   * @param tree Tree whose LM is be constructed.
   */
  private void computeLMs(MyTreeResult tree) {

    // Build Node LMs
    boolean first = true;
    for(Node n:tree.getNodes()) {
      Document doc = new Document(n.getId(),false);
      doc.buildLM();
      if(first){
        first = false;
        this.nodeLMs=doc.lm;
      }
      else{
        this.nodeLMs.merge(doc.lm);
      }
    }

    //Build edge LMs
    first = true;
    for(Integer n:tree.getEdges()) {
      Document doc = new Document(n,true);
      doc.buildLM();
      if(first){
        first = false;
        this.edgeLMs=doc.lm;
      }
      else{
        this.edgeLMs.merge(doc.lm);
      }
    }
  }

  @Override
  public int compareTo(TreeLM o) {
    return 1;
  }

  @Override
  public String toString() {
    return "TreeLM [nodeLMs=" + nodeLMs + ", edgeLMs=" + edgeLMs + "]";
  }

  public int getRank() {
    return rank;
  }

  public NodeLM getNodeLM() {
    return (NodeLM)this.nodeLMs;
  }

  public EdgeLM getEdgeLM() {
    return (EdgeLM)this.edgeLMs;
  }

}
