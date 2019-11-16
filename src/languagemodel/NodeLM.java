package languagemodel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Language Model for an entity or node in the graph.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class NodeLM extends LM{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public NodeLM(int id) {
    this.ID = id;
    this.ispred = false;
    this.unigram0 = new ConcurrentHashMap<Integer,Integer>();
    this.bigram = new ConcurrentHashMap<String,Integer>();
    this.totalUnigram0 = 0;
    this.totalBigram = 0;
  }

  @Override
  public String toString() {
    return "NodeLM [nodeID=" + ID + ", ispred=" + ispred + ", unigram=" + unigram0 + ", bigram=" + bigram + "]";
  }

}
