package languagemodel;

import java.util.List;

/**
 * Document and LM for nodes and edges.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class Document {
  Integer ID;
  boolean ispred;
  List<Integer> triples;
  public LM lm;

  public Document(int id, boolean ispred) {
    this.ID = id;
    this.ispred = ispred;
    this.triples = fetchTriples(id);
    if(ispred)
      this.lm = new EdgeLM(id);
    else
      this.lm = new NodeLM(id);
  }

  private List<Integer> fetchTriples(int id) {
    return DefaultLM.nodesToTripleID.get(id);
  }

  public void buildLM() {
    if(ispred)
      buildEdgeLM();
    else
      buildNodeLM();
  }

  private void buildNodeLM() {
    //System.out.println("Building NodeLM-->");
    for(int i: this.triples) {
      Triple t = DefaultLM.graph.get(i);
      Integer sub = t.subject;
      Integer pred = t.predicate;
      Integer obj = t.object;

      //Node Unigram
      int count = 1;
      if(this.lm.unigram0.containsKey(sub)) {
        count = this.lm.unigram0.get(sub)+1;
      }
      this.lm.unigram0.put(sub,count);
      this.lm.totalUnigram0++;

      count = 1;
      if(this.lm.unigram0.containsKey(obj)) {
        count = this.lm.unigram0.get(obj)+1;
      }
      this.lm.unigram0.put(obj,count);
      this.lm.totalUnigram0++;

      //Node Bigram
      count = 1;
      if(this.lm.bigram.containsKey(sub.toString()+"-"+pred.toString())) {
        count = this.lm.bigram.get(sub.toString()+"-"+pred.toString())+1;
      }
      this.lm.bigram.put(sub.toString()+"-"+pred.toString(),count);
      this.lm.totalBigram++;

      count = 1;
      if(this.lm.bigram.containsKey(pred.toString()+"-"+obj.toString())) {
        count = this.lm.bigram.get(pred.toString()+"-"+obj.toString())+1;
      }
      this.lm.bigram.put(pred.toString()+"-"+obj.toString(),count);
      this.lm.totalBigram++;
    }
    //System.out.println("Done with NodeLM.");
  }

  private void buildEdgeLM() {
    //System.out.println("Building EdgeLM-->");
    for(int i: this.triples) {
      Triple t = DefaultLM.graph.get(i);
      Integer sub = t.subject;
      Integer obj = t.object;

      //Relation Unigram - Subjects and Objects
      int count = 1;
      if(this.lm.unigram0.containsKey(sub)) {
        count = this.lm.unigram0.get(sub)+1;
      }
      this.lm.unigram0.put(sub,count);
      this.lm.totalUnigram0++;

      count = 1;
      if(this.lm.unigram1.containsKey(obj)) {
        count = this.lm.unigram1.get(obj)+1;
      }
      this.lm.unigram1.put(obj,count);
      this.lm.totalUnigram1++;


      //Relation Bigram
      count = 1;
      if(this.lm.bigram.containsKey(sub.toString()+"-"+obj.toString())) {
        count = this.lm.bigram.get(sub.toString()+"-"+obj.toString())+1;
      }
      this.lm.bigram.put(sub.toString()+"-"+obj.toString(),count);
      this.lm.totalBigram++;
    }
    //System.out.println("Done with EdgeLM.");
  }
}
