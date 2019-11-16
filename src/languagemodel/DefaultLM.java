package languagemodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.Config;

/**
 * Default LM values for the graph to use for smoothing.
 * 
 * The default values are read from files using {@link #readLMsFromFile()}.
 * 
 * The construction from scratch can be done using: {@link #computeUnigramBigramLM()} 
 * and written to file using: {@link #writeUnigramBigramLMsToFile()}.
 * 
 * TODO Maybe the objects should not be static for adaptability to any graph in general.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class DefaultLM {

  public static List<Triple> graph; // List of triples in the graph dataset.
  public static Map<Integer,List<Integer>> nodesToTripleID; // Maps id of nodes/predicates to id of the triples having them.

  public static Map<Integer,Integer> nodeUnigramsList;
  public static Map<String,Integer> nodeBigramsList;
  public static Map<Integer,Integer> relationSubjectList;
  public static Map<Integer,Integer> relationObjectList;
  public static Map<String,Integer> relationBigramsList;
  public static long totalNodeUnigram, totalNodeBigram, totalRelationSubject, totalRelationObject, totalRelationBigram;

  static{
	// Compute LMs once and then read them from file.
    readTriples();
    initializeGraph();
    //computeUnigramBigramLM();
    //writeUnigramBigramLMsToFile();
    readLMsFromFile();
  }

  private static void readTriples() {
    DefaultLM.graph = new ArrayList<Triple>();
    System.out.println("DefaultLM:Reading triples into memory----->");
    BufferedReader br = null;

    try {
      br = new BufferedReader(new FileReader(Config.graphFileName));
      String line;

      while((line=br.readLine())!=null)
      {
        String[] nodes = line.split("\t");
        List<String> vals = Arrays.asList(nodes);
        if(vals.size()!=3) continue;
        Integer s = Integer.parseInt(vals.get(0));
        Integer p = Integer.parseInt(vals.get(1));
        Integer o = Integer.parseInt(vals.get(2));

        Triple t = new Triple(s,p,o);
        DefaultLM.graph.add(t);
      }

    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }finally{
      try {
        if (br != null)
          br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    System.out.println("DefaultLM:All triples in memory now!");
  }

  private static void initializeGraph() {
    System.out.println("DefaultLM:Initializing map of nodes to triples----->");
    DefaultLM.nodesToTripleID = new HashMap<Integer, List<Integer>>();
    for(int i=0;i<DefaultLM.graph.size();i++) {
      Triple t = DefaultLM.graph.get(i);
      Integer s = t.subject;
      Integer p = t.predicate;
      Integer o = t.object;

      List<Integer> matchedTriples = null;
      if(DefaultLM.nodesToTripleID.containsKey(s))
        matchedTriples = DefaultLM.nodesToTripleID.get(s);
      else
        matchedTriples = new ArrayList<Integer>();
      matchedTriples.add(i);
      DefaultLM.nodesToTripleID.put(s, matchedTriples);

      matchedTriples = null;
      if(DefaultLM.nodesToTripleID.containsKey(p))
        matchedTriples = DefaultLM.nodesToTripleID.get(p);
      else
        matchedTriples = new ArrayList<Integer>();
      matchedTriples.add(i);
      DefaultLM.nodesToTripleID.put(p, matchedTriples);

      matchedTriples = null;
      if(DefaultLM.nodesToTripleID.containsKey(o))
        matchedTriples = DefaultLM.nodesToTripleID.get(o);
      else
        matchedTriples = new ArrayList<Integer>();
      matchedTriples.add(i);
      DefaultLM.nodesToTripleID.put(o, matchedTriples);
    }
    System.out.println("DefaultLM:Initialization done!");
  }

  private static void readLMsFromFile() {
    BufferedReader br = null;
    try {
      System.out.println("Starting to read the node unigrams from file----->");
      br = new BufferedReader(new FileReader(Config.nodesUnigramFile));
      String line;
      long total = 0;
      DefaultLM.nodeUnigramsList = new HashMap<Integer,Integer>();
      while((line=br.readLine())!=null){
        String[] vals = line.split("\t");
        if(vals.length>2)
          throw new IllegalArgumentException("Number of values > 2 when 2 args expected.");
        Integer node = Integer.parseInt(vals[0]);
        Integer val = Integer.parseInt(vals[1]);
        total+=val;
        DefaultLM.nodeUnigramsList.put(node, val);
      }
      DefaultLM.totalNodeUnigram = total;
      br.close();
      System.out.println("Done with reading the node unigrams from file.");

      System.out.println("Starting to read the node bigrams from file----->");
      br = new BufferedReader(new FileReader(Config.nodesBigramFile));
      total = 0;
      DefaultLM.nodeBigramsList = new HashMap<String,Integer>();
      while((line=br.readLine())!=null){
        String[] vals = line.split("\t");
        if(vals.length>2)
          throw new IllegalArgumentException("Number of values > 2 when 2 args expected.");
        String node = vals[0];
        Integer val = Integer.parseInt(vals[1]);
        total+=val;
        DefaultLM.nodeBigramsList.put(node, val);
      }
      DefaultLM.totalNodeBigram = total;
      br.close();
      System.out.println("Done with reading the node bigrams from file.");

      System.out.println("Starting to read the relation subject unigrams from file----->");
      br = new BufferedReader(new FileReader(Config.relationSubjectFile));
      total = 0;
      DefaultLM.relationSubjectList = new HashMap<Integer,Integer>();
      while((line=br.readLine())!=null){
        String[] vals = line.split("\t");
        if(vals.length>2)
          throw new IllegalArgumentException("Number of values > 2 when 2 args expected.");
        Integer node = Integer.parseInt(vals[0]);
        Integer val = Integer.parseInt(vals[1]);
        total+=val;
        DefaultLM.relationSubjectList.put(node, val);
      }
      DefaultLM.totalRelationSubject = total;
      br.close();
      System.out.println("Done with reading the relation subject unigrams from file.");

      System.out.println("Starting to read the relation object unigrams from file----->");
      br = new BufferedReader(new FileReader(Config.relationObjectFile));
      total = 0;
      DefaultLM.relationObjectList = new HashMap<Integer,Integer>();
      while((line=br.readLine())!=null){
        String[] vals = line.split("\t");
        if(vals.length>2)
          throw new IllegalArgumentException("Number of values > 2 when 2 args expected.");
        Integer node = Integer.parseInt(vals[0]);
        Integer val = Integer.parseInt(vals[1]);
        total+=val;
        DefaultLM.relationObjectList.put(node, val);
      }
      DefaultLM.totalRelationObject = total;
      br.close();
      System.out.println("Done with reading the relation object unigrams from file.");

      System.out.println("Starting to read the relation bigrams from file----->");
      br = new BufferedReader(new FileReader(Config.relationBigramFile));
      total = 0;
      DefaultLM.relationBigramsList = new HashMap<String,Integer>();
      while((line=br.readLine())!=null){
        String[] vals = line.split("\t");
        if(vals.length>2)
          throw new IllegalArgumentException("Number of values > 2 when 2 args expected.");
        String node = vals[0];
        Integer val = Integer.parseInt(vals[1]);
        total+=val;
        DefaultLM.relationBigramsList.put(node, val);
      }
      DefaultLM.totalRelationBigram = total;
      br.close();
      System.out.println("Done with reading the relation bigrams from file.");

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      if(br!=null)
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }

  @SuppressWarnings("unused")
  private static void writeUnigramBigramLMsToFile() {
    BufferedWriter wr = null;
    try {
      //Write the node unigrams to file.
      System.out.println("Starting to write the node unigrams to file----->");

      wr = new BufferedWriter(new FileWriter(Config.nodesUnigramFile));
      for(Entry<Integer, Integer> e:DefaultLM.nodeUnigramsList.entrySet()){
        wr.write(e.getKey()+"\t"+e.getValue()+"\n");
      }
      wr.close();
      System.out.println("Done with writing the node unigrams to file.");

      //Write the node bigrams to file.
      System.out.println("Starting to write the node bigrams to file----->");

      wr = new BufferedWriter(new FileWriter(Config.nodesBigramFile));
      for(Entry<String, Integer> e:DefaultLM.nodeBigramsList.entrySet()){
        wr.write(e.getKey()+"\t"+e.getValue()+"\n");
      }
      wr.close();
      System.out.println("Done with writing the node bigrams to file.");

      //Write the relation subject unigrams to file.
      System.out.println("Starting to write the relation subject unigrams to file----->");

      wr = new BufferedWriter(new FileWriter(Config.relationSubjectFile));
      for(Entry<Integer, Integer> e:DefaultLM.relationSubjectList.entrySet()){
        wr.write(e.getKey()+"\t"+e.getValue()+"\n");
      }
      wr.close();
      System.out.println("Done with writing the relation subject unigrams to file.");


      //Write the relation object unigrams to file.
      System.out.println("Starting to write the relation object unigrams to file----->");

      wr = new BufferedWriter(new FileWriter(Config.relationObjectFile));
      for(Entry<Integer, Integer> e:DefaultLM.relationObjectList.entrySet()){
        wr.write(e.getKey()+"\t"+e.getValue()+"\n");
      }
      wr.close();
      System.out.println("Done with writing the relation object unigrams to file.");


      //Write the relation bigrams to file.
      System.out.println("Starting to write the relation bigrams to file----->");

      wr = new BufferedWriter(new FileWriter(Config.relationBigramFile));
      for(Entry<String, Integer> e:DefaultLM.relationBigramsList.entrySet()){
        wr.write(e.getKey()+"\t"+e.getValue()+"\n");
      }
      wr.close();
      System.out.println("Done with writing the relation bigrams to file.");

    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  @SuppressWarnings("unused")
  private static void computeUnigramBigramLM() {
    System.out.println("Computing unigrams and bigrams counts----->");

    BufferedReader br = null;

    try {
      br = new BufferedReader(new FileReader(Config.graphFileName));
      String line;
      while((line=br.readLine())!=null)
      {
        String[] nodes = line.split("\t");
        List<String> vals = Arrays.asList(nodes);
        if(vals.size()!=3) continue;
        Integer sub = Integer.parseInt(vals.get(0));
        Integer pred = Integer.parseInt(vals.get(1));
        Integer obj = Integer.parseInt(vals.get(2));

        //Node Unigram
        int count = 1;
        if(DefaultLM.nodeUnigramsList.containsKey(sub)) {
          count = DefaultLM.nodeUnigramsList.get(sub)+1;
        }
        DefaultLM.nodeUnigramsList.put(sub,count);

        count = 1;
        if(DefaultLM.nodeUnigramsList.containsKey(obj)) {
          count = DefaultLM.nodeUnigramsList.get(obj)+1;
        }
        DefaultLM.nodeUnigramsList.put(obj,count);

        //Node Bigram
        count = 1;
        if(DefaultLM.nodeBigramsList.containsKey(sub.toString()+"-"+pred.toString())) {
          count = DefaultLM.nodeBigramsList.get(sub.toString()+"-"+pred.toString())+1;
        }
        DefaultLM.nodeBigramsList.put(sub.toString()+"-"+pred.toString(),count);

        count = 1;
        if(DefaultLM.nodeBigramsList.containsKey(pred.toString()+"-"+obj.toString())) {
          count = DefaultLM.nodeBigramsList.get(pred.toString()+"-"+obj.toString())+1;
        }
        DefaultLM.nodeBigramsList.put(pred.toString()+"-"+obj.toString(),count);

        //Relation Unigram - Subjects and Objects
        count = 1;
        if(DefaultLM.relationSubjectList.containsKey(sub)) {
          count = DefaultLM.relationSubjectList.get(sub)+1;
        }
        DefaultLM.relationSubjectList.put(sub,count);

        count = 1;
        if(DefaultLM.relationObjectList.containsKey(obj)) {
          count = DefaultLM.relationObjectList.get(obj)+1;
        }
        DefaultLM.relationObjectList.put(obj,count);


        //Relation Bigram
        count = 1;
        if(DefaultLM.relationBigramsList.containsKey(sub.toString()+"-"+obj.toString())) {
          count = DefaultLM.relationBigramsList.get(sub.toString()+"-"+obj.toString())+1;
        }
        DefaultLM.relationBigramsList.put(sub.toString()+"-"+obj.toString(),count);

      }
    } catch (NumberFormatException | IOException e) {
      e.printStackTrace();
    }finally{
      try {
        if (br != null)
          br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    System.out.println("Done computing unigrams and bigrams counts.");
  }
}
