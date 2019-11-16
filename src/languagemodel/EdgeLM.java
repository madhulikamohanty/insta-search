package languagemodel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Language Model for an edge/relation in the graph.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class EdgeLM extends LM{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public EdgeLM(int id) {
    this.ID = id;
    this.ispred = true;
    this.unigram0 = new ConcurrentHashMap<Integer,Integer>();
    this.unigram1 = new ConcurrentHashMap<Integer,Integer>();
    this.bigram = new ConcurrentHashMap<String,Integer>();
    this.totalUnigram0 = 0;
    this.totalUnigram1 = 0;
    this.totalBigram = 0;
  }
  
  @Override
  public String toString() {
    return "EdgeLM [ID=" + ID + ", ispred=" + ispred + ", subUnigram=" + unigram0 + ", objUnigram=" + unigram1 + ", bigram=" + bigram 
        + "]";
  }
}
