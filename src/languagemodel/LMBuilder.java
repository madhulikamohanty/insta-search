package languagemodel;

/**
 * Class to build LMs for the graph.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class LMBuilder {

  public static void main(String[] args) {
    Document doc1 = new Document(1, false); 
    long start = System.currentTimeMillis();
    doc1.buildLM();
    long end = System.currentTimeMillis();
    System.out.println("Time to build LM:"+(end-start)+"ms.");
    System.out.println(doc1.lm);
    
    Document doc2 = new Document(3, false); 
    start = System.currentTimeMillis();
    doc2.buildLM();
    end = System.currentTimeMillis();
    System.out.println("Time to build LM:"+(end-start)+"ms.");
    System.out.println(doc2.lm);
    
  }
}
