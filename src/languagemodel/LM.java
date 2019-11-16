package languagemodel;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Language Model.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public abstract class LM implements Serializable{

  /**
   * Required for serializability.
   */
  private static final long serialVersionUID = 1L;
  Integer ID;
  boolean ispred; // True if the LM corresponds to a predicate.
  ConcurrentHashMap<Integer, Integer> unigram0; //Subject Unigram for EdgeLM, unigram for NodeLM.
  ConcurrentHashMap<Integer, Integer> unigram1; //Object unigrams for EdgeLM, null for NodeLM.
  ConcurrentHashMap<String, Integer> bigram;
  long totalUnigram0, totalUnigram1, totalBigram;

  @Override
  public String toString() {
    if(this.unigram1==null)
      return "LM [ID=" + ID + ", ispred=" + ispred + ", unigram0=" + unigram0 + ", bigram="
      + bigram + ", totalUnigram0=" + totalUnigram0 + ", totalBigram="
      + totalBigram + "]";
    else
      return "LM [ID=" + ID + ", ispred=" + ispred + ", unigram0=" + unigram0 + ", unigram1=" + unigram1 + ", bigram="
      + bigram + ", totalUnigram0=" + totalUnigram0 + ", totalUnigram1=" + totalUnigram1 + ", totalBigram="
      + totalBigram + "]";
  }
  
  /**
   * Merge two LMs.
   * @param lm The {@link LM} to be merged.
   */
  public void merge(LM lm) {
    //Merge unigram0
    for(Integer unigram:lm.unigram0.keySet()){
      if(this.unigram0.containsKey(unigram)){
        Integer otherVal=lm.unigram0.get(unigram);
        Integer newVal=this.unigram0.get(unigram)+otherVal;
        this.unigram0.put(unigram, newVal);
      }
      else
        this.unigram0.put(unigram, lm.unigram0.get(unigram));
    }
    this.totalUnigram0 = this.totalUnigram0 + lm.totalUnigram0;

    //Merge unigram1
    if(ispred){
      for(Integer unigram:lm.unigram1.keySet()){
        if(this.unigram1.containsKey(unigram)){
          Integer otherVal=lm.unigram1.get(unigram);
          Integer newVal=this.unigram1.get(unigram)+otherVal;
          this.unigram1.put(unigram, newVal);
        }
        else
          this.unigram1.put(unigram, lm.unigram1.get(unigram));
      }
      this.totalUnigram1 = this.totalUnigram1 + lm.totalUnigram1;
    }

    //Merge bigram
    for(String bigram:lm.bigram.keySet()){
      if(this.bigram.containsKey(bigram)){
        Integer otherVal=lm.bigram.get(bigram);
        Integer newVal=this.bigram.get(bigram)+otherVal;
        this.bigram.put(bigram, newVal);
      }
      else
        this.bigram.put(bigram, lm.bigram.get(bigram));
    }
    this.totalBigram = this.totalBigram + lm.totalBigram;
  }

  /**
   * Computes the Jensen-Shannon Divergence from the other LM. The JS divergence is a popular method of measuring the similarity between two probability distributions. 
   * It is also known as information radius or total divergence to the average. The JS divergence is a smoothed version of the Kullback-Leibler divergence. 
   * It is defined by:
   * JS(P||Q) = (D(P||M) + D(Q||M)) / 2 
   * where M = (P+Q)/2 and D(·||·) is KL divergence. 
   * @param lm The other {@link LM}.
   * @return JS-Divergence value.
   */
  public double computeJSD(LM lm){
    double divergence = 0.0;
    //Compute unigram0 divergence.
    for(Integer unigram:lm.unigram0.keySet()){ // Check for unigrams in other 'lm', pick default val for this LM when not found.
      double lm1=0.0,lm2=0.0;
      if(this.unigram0.containsKey(unigram)){
        lm1+=0.5*(this.unigram0.get(unigram)*1.0/this.totalUnigram0);
      }
      lm2+=0.5*(lm.unigram0.get(unigram)*1.0/lm.totalUnigram0);
      if(ispred) {
        double defaultlm=0.5*(DefaultLM.relationSubjectList.get(unigram)*1.0/DefaultLM.totalRelationSubject);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      else {
        double defaultlm=0.5*(DefaultLM.nodeUnigramsList.get(unigram)*1.0/DefaultLM.totalNodeUnigram);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      //System.out.println("u0:lm1:"+lm1);
      //System.out.println("u0:lm2:"+lm2);
      divergence+=JSD(lm1,lm2);
    }

    // Now check for unigrams not in other 'lm' but in this LM.
    Set<Integer> unigrams0 = this.unigram0.keySet();
    unigrams0.removeAll(lm.unigram0.keySet());
    for(Integer unigram:unigrams0){
      double lm1=0.0,lm2=0.0;
      lm1+=0.5*(this.unigram0.get(unigram)*1.0/this.totalUnigram0);
      if(ispred) {
        double defaultlm=0.5*(DefaultLM.relationSubjectList.get(unigram)*1.0/DefaultLM.totalRelationSubject);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      else {
        double defaultlm=0.5*(DefaultLM.nodeUnigramsList.get(unigram)*1.0/DefaultLM.totalNodeUnigram);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      //System.out.println("u0':lm1:"+lm1);
      //System.out.println("u0':lm2:"+lm2);
      divergence+=JSD(lm1,lm2);
    }
    //Compute unigram1 divergence.
    if(ispred){
      for(Integer unigram:lm.unigram1.keySet()){
        double lm1=0.0,lm2=0.0;
        if(this.unigram1.containsKey(unigram)){
          lm1+=0.5*(this.unigram1.get(unigram)*1.0/this.totalUnigram1);
        }
        lm2+=0.5*(lm.unigram1.get(unigram)*1.0/lm.totalUnigram1);
        double defaultlm=0.5*(DefaultLM.relationObjectList.get(unigram)*1.0/DefaultLM.totalRelationObject);
        lm1+=defaultlm;
        lm2+=defaultlm;
        //System.out.println("u1:lm1:"+lm1);
        //System.out.println("u1:lm2:"+lm2);
        divergence+=JSD(lm1,lm2);
      }
      Set<Integer> unigrams1 = this.unigram1.keySet();
      unigrams1.removeAll(lm.unigram1.keySet());
      for(Integer unigram:unigrams1){
        double lm1=0.0,lm2=0.0;
        lm1+=0.5*(this.unigram1.get(unigram)*1.0/this.totalUnigram1);
        double defaultlm=0.5*(DefaultLM.relationObjectList.get(unigram)*1.0/DefaultLM.totalRelationObject);
        lm1+=defaultlm;
        lm2+=defaultlm;
        //System.out.println("u1':lm1:"+lm1);
        //System.out.println("u1':lm2:"+lm2);
        divergence+=JSD(lm1,lm2);
      }
    }
    //Compute bigram divergence.
    for(String bigram:lm.bigram.keySet()){
      double lm1=0.0,lm2=0.0;
      if(this.bigram.containsKey(bigram)){
        lm1+=0.5*(this.bigram.get(bigram)*1.0/this.totalBigram);
      }
      lm2+=0.5*(lm.bigram.get(bigram)*1.0/lm.totalBigram);
      if(ispred) {
        double defaultlm=0.5*(DefaultLM.relationBigramsList.get(bigram)*1.0/DefaultLM.totalRelationBigram);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      else{
        double defaultlm=0.5*(DefaultLM.nodeBigramsList.get(bigram)*1.0/DefaultLM.totalNodeBigram);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      //System.out.println("b:lm1:"+lm1);
      //System.out.println("b:lm2:"+lm2);
      divergence+=JSD(lm1,lm2);
    }
    Set<String> bigrams = this.bigram.keySet();
    bigrams.removeAll(lm.bigram.keySet());
    for(String bigram:bigrams){
      double lm1=0.0,lm2=0.0;
      lm1+=0.5*(this.bigram.get(bigram)*1.0/this.totalBigram);
      //System.out.println("without default b':lm1:"+lm1);
      //System.out.println("val1:"+this.bigram.get(bigram));
      //System.out.println("val2:"+this.totalBigram);
      if(ispred) {
        double defaultlm=0.5*(DefaultLM.relationBigramsList.get(bigram)*1.0/DefaultLM.totalRelationBigram);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      else{
        double defaultlm=0.5*(DefaultLM.nodeBigramsList.get(bigram)*1.0/DefaultLM.totalNodeBigram);
        lm1+=defaultlm;
        lm2+=defaultlm;
      }
      //System.out.println("b':lm1:"+lm1);
      //System.out.println("b':lm2:"+lm2);
      divergence+=JSD(lm1,lm2);
    }
    //System.err.println("Divergence:"+divergence);
    return divergence;
  }

  /**
   * Computes the JS-divergence for a particular value in the vector. It is defined by:
   * JS(P(i)||Q(i)) = (D(P(i)||M(i)) + D(Q(i)||M(i))) / 2 
   * where M(i) = (P(i)+Q(i))/2 and D(·||·) is KL divergence. 
   * @param lm1 The value of first vector, P(i).
   * @param lm2 The value of second vector, Q(i).
   * @return The JS-divergence value.
   */
  private double JSD(double lm1, double lm2) {
    double m = 0.5*(lm1+lm2);
    double jsd = (lm1*Math.log(lm1/m)); //Natural log, hence, max value one can get is ln(2).
    jsd = jsd + (lm2*Math.log(lm2/m));
    return jsd;
  }

}
