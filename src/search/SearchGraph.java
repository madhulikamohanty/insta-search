package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.common.collect.MinMaxPriorityQueue;

import languagemodel.Triple;
import paths.KNeighbourhoodComputer;
import paths.SelectivePathComputer;
import search.utils.MyTreeResult;
import search.utils.Node;
import utils.Config;
import utils.FaginsAlgorithm;
import utils.MyBloomFilter;
import utils.NodeEntry;
import utils.NodeEntryComparator;
import utils.PathDistance;

/**
 * Class to search for answer trees based on input keyword query.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class SearchGraph {

  public static void main(String[] args) {

    SearchGraph sg = new SearchGraph();
    String query = "Angelina_Jolie Brad_Pitt";
    System.out.println("The expected number of answers is:" + sg.processQuery(query,2));

  }

  /**
   * Processes a query file and prints output.
   * @param queryFile The query file to be processed.
   */
  @SuppressWarnings("unused")
  private void processQueryFile(String queryFile) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(queryFile));
      String line;
      while((line=br.readLine())!=null){
        System.out.println("Processing Query:"+line);
        System.out.println(processQuery(line,25));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }finally{processQueryFile(Config.queryFile);
    if(br!=null)
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Processes the user query.
   * @param query The query to be processed.
   * @param numAnswers Number of answers to be returned. -1 for all.
   * @return Answers in {@link String} format.
   */
  public String processQuery(String query, int numAnswers) {
    List<Integer> queryNodes = getQueryNodes(query);
    // Find shortest paths between IDs.
    return getAnswerTree(queryNodes,numAnswers);
  }

  /**
   * Fetches list of queried nodes from the keywords.
   * 
   * @param query String of keyword query entities.
   * @return List of integer ids of the mapped nodes.
   */
  private List<Integer> getQueryNodes(String query) {
    List<Integer> queryNodes = new ArrayList<Integer>();
    String[] entities = query.split(" ");
    for (String entity : entities){
      entity="<"+entity.toLowerCase()+">"; // toLowerCase() to ensure compatibility.
      if(SelectivePathComputer.nameToNum.containsKey(entity)){
        queryNodes.add(SelectivePathComputer.nameToNum.get(entity));
      }
      else
        throw new IllegalArgumentException("Did not find existing id for queried node:"+entity);
    }
    return queryNodes;
  }

  /**
   * Computes and prints the answer tree for the queried nodes.
   * @param queryNodes List of nodes in the keyword query.
   * @param numAnswers Number of answers to be returned. -1 for all.
   * @return Answers in {@link String} format.
   */
  private String getAnswerTree(List<Integer> queryNodes, int numAnswers) {
    //long start = (new Date()).getTime();
    List<Map<Integer, PathDistance>> shortestPaths = new ArrayList<Map<Integer, PathDistance>>(); // Maintains list of reachable nodes and their paths to each queried node.
    Set<Integer> reachableNodes = new HashSet<Integer>(); // Set of nodes reachable from each queried node (initially empty).
    String toBePrinted="";
    long timeToRead = 0;
    long timeToIntersect = 0;
    long start1, start2, end1, end2; // To be used for computing time taken.
    boolean initial = true;
    for (int nodeID:queryNodes){ //Fetch common nodes.
      try {
        BufferedReader br = new BufferedReader(new FileReader
            (new File(Config.shortestPathFolderName+nodeID)));
        String line = "";
        Map<Integer, PathDistance> distances = new HashMap<Integer,PathDistance> ();
        Set<Integer> currentReachableNodes = new HashSet<Integer>();
        start1 = (new Date()).getTime();
        while((line=br.readLine())!=null){
          String[] nodes = line.split("\t");
          int key = Integer.parseInt(nodes[0]);
          List<Integer> path = new ArrayList<Integer>();
          for(int i =1;i<nodes.length-1;i++)
            path.add(Integer.parseInt(nodes[i]));
          int distance = Integer.parseInt(nodes[nodes.length-1]);
          distances.put(key, new PathDistance(path,distance));
          if(initial){
            reachableNodes.add(key);
          }
          else{
            currentReachableNodes.add(key);
          }
        }
        end1 = (new Date()).getTime();
        timeToRead+= end1-start1;
        br.close();
        shortestPaths.add(distances);
        if(!initial){
          start2 = (new Date()).getTime();
          reachableNodes.retainAll(currentReachableNodes);
          end2 = (new Date()).getTime();
          timeToIntersect+= end2-start2;
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      initial = false;
    }

    //long endIntersect = (new Date()).getTime();
    System.out.println("Reading done in " + timeToRead + "ms.");
    System.out.println("Computing intersection done in " + timeToIntersect + "ms.");
    toBePrinted+="Expected total number of answers:"+reachableNodes.size()+"\n";
    toBePrinted+="Expected top answers are as follows:\n";

    //start1 = (new Date()).getTime();
    toBePrinted+=computeTopAnswers(queryNodes,reachableNodes,shortestPaths,numAnswers);
    //end1 = (new Date()).getTime();
    //long timeToTop2 = end1-start1;
    //System.out.println("Top-2 computed in " + timeToTop2 + "ms.");

    return toBePrinted;
  }

  /**
   * Fagin's algorithm to compute top answers from set intersection.
   * @param queryNodes List of keyword nodes.
   * @param reachableNodes List of nodes reachable from all keyword nodes.
   * @param shortestPaths Shortest paths to reachable nodes from each keyword node.
   * @param numAnswers Number of top answers to be computed.
   * @return The result string of answers to be printed.
   */
  private String computeTopAnswers(List<Integer> queryNodes, Set<Integer> reachableNodes, List<Map<Integer, PathDistance>> shortestPaths,
      int numAnswers) {
    String toBePrinted="";
    List<PriorityQueue<NodeEntry>> prqs = new ArrayList<PriorityQueue<NodeEntry>>();
    for(int c=0;c<queryNodes.size();c++){
      PriorityQueue<NodeEntry> prq = new PriorityQueue<NodeEntry>(reachableNodes.size(), new NodeEntryComparator());
      prqs.add(prq);
    }
    for(int node:reachableNodes){
      for(int i=0;i<queryNodes.size();i++){
        Map<Integer, PathDistance> distances = shortestPaths.get(i);
        prqs.get(i).add(new NodeEntry(node,distances.get(node).distance));
      }
    }

    FaginsAlgorithm fg = new FaginsAlgorithm(prqs, numAnswers, shortestPaths);
    
    long start1 = (new Date()).getTime();
    MinMaxPriorityQueue<NodeEntry> topNodes = fg.getTopKNodes();
    long end1 = (new Date()).getTime();
    long timeToTop2 = end1-start1;
    System.out.println("Top-2 computed in " + timeToTop2 + "ms.");

    


    /*int count = 1;
    for(NodeEntry ne:topNodes){
      int nodeID = ne.nodeID;
      toBePrinted+="------------------------ Answer-" + count + " ------------------------\n";
      int totalLength = 0;
      for(int i=0;i<queryNodes.size();i++){
        Map<Integer, PathDistance> distances = shortestPaths.get(i);
        toBePrinted+=SelectivePathComputer.numToName.get(queryNodes.get(i)) + " --> " + 
            getPathString(distances.get(nodeID).path) + SelectivePathComputer.numToName.get(nodeID) + 
            " Length:" + distances.get(nodeID).distance+"\n";
        totalLength+=distances.get(nodeID).distance;
      }
      toBePrinted+="Total length:"+totalLength+"\n";
      count++;
    }*/


    List<MyTreeResult> resultHeap = new ArrayList<MyTreeResult>(numAnswers);
    for(NodeEntry ne:topNodes){
      int nodeID = ne.nodeID;
      MyTreeResult res=new MyTreeResult();
      Node n = new Node(nodeID);
      res.setRoot(n);
      for(int i=0;i<queryNodes.size();i++){
        List<Integer> intermediateNodes = shortestPaths.get(i).get(nodeID).path;
        Node prev = new Node(queryNodes.get(i));
        for(int intNode: intermediateNodes) {
          Node nxt = new Node(intNode);
          Iterator<Triple> iter=BidirSearch.dg.find(nxt, BidirSearch.ANY_PREDICATE, prev);
          if(iter.hasNext()){ //Changed while to if to take only one triple.
            Triple q=iter.next();
            res.addEdge(new Node(q.getSubject()),q.getPredicate(),new Node(q.getObject()));
          }
          prev=nxt;
        }

        //For the last hop between the last node and root node.
        Iterator<Triple> iter=BidirSearch.dg.find(new Node(nodeID), BidirSearch.ANY_PREDICATE, prev);
        if(iter.hasNext()){ //Changed while to if to take only one triple.
          Triple q=iter.next();
          res.addEdge(new Node(q.getSubject()),q.getPredicate(),new Node(q.getObject()));
        }
      }
      resultHeap.add(res);
    }

    // Write to JSON File.
    TreeResultToJSONFile.saveToFile(resultHeap,Config.resultsDir+Config.instaresultsFile);

    return toBePrinted;
  }

  @SuppressWarnings("unused")
  private static String getPathString(List<Integer> path) {
    String pathString = "";
    for(int node: path){
      pathString+=SelectivePathComputer.numToName.get(node)+" --> ";
    }
    return pathString;
  }


}
