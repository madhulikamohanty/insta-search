package paths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import utils.Config;
import utils.PathDistance;

/**
 * Class to run 'k' hop neighborhood computation for all nodes in a graph.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class KNeighbourhoodComputer implements Runnable {

  public static final int depth_level = 2;
  public static final ImmutableValueGraph<Integer, ArrayList<Integer>> graph=readGraph();
  //public static final CompactGraph graph = readCompactGraph();
  public static final List<Integer> nodes = new ArrayList<Integer>(KNeighbourhoodComputer.graph.nodes());
  //public static final Set<Integer> computedNodes = getSeenNodes();
  int startIndex,endIndex;

  public KNeighbourhoodComputer(int startIndex, int endIndex) {
    this.startIndex=startIndex;
    this.endIndex=endIndex;
  }


  public KNeighbourhoodComputer() {
    this.startIndex=0;
    this.endIndex=0;
  }


  public void run() {
    System.out.println("Thread with startIndex="+startIndex+" and endIndex="+endIndex+" is starting now.");
    for(int i=startIndex;i<=endIndex;i++){
      if(checkIfComputed(KNeighbourhoodComputer.nodes.get(i)))//KNeighbourhoodComputer.computedNodes.contains(KNeighbourhoodComputer.nodes.get(i)))
        continue;
      Map<Integer, PathDistance> distances;
      try {
        distances = createShortestPathsForNode(KNeighbourhoodComputer.nodes.get(i));
        dumpPathsToFile(KNeighbourhoodComputer.nodes.get(i), distances);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("Thread with startIndex="+startIndex+" and endIndex="+endIndex+" has committed.");
  }

  /**
   * Checks if the shortest path of the node has already been computed.
   * @param nodeID Node to be checked.
   * @return {@code true} if already computed, {@code false} otherwise.
   */
  private boolean checkIfComputed(Integer nodeID) {
    File tmpFile = new File(Config.shortestPathFolderName+nodeID.toString());
    return tmpFile.exists();
  }


  /**
   * Writes the node and its shortest paths and distances to other nodes onto a file.
   * 
   * @param i The node whose file is to be written.
   * @param distances The map containing shortest paths and distances from other nodes.
   */
  public void dumpPathsToFile(int id, Map<Integer, PathDistance> distances) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter
          (new File(Config.shortestPathFolderName+id)));
      for(Integer neighbour : distances.keySet()){
        Integer distance = distances.get(neighbour).distance;
        List<Integer> path = distances.get(neighbour).path;
        String nodePath="";
        for(int node:path){
          nodePath+="\t"+node;
        }
        bw.write(neighbour + nodePath + "\t" + distance+"\n");
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes the node and its distances to other nodes onto a file.
   * 
   * @param i The node whose file is to be written.
   * @param distances The map containing distances from other nodes.
   */
  public void dumpToFile(int i, Map<Integer, Integer> distances) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter
          (new File(Config.shortestDistanceFolderName+i)));
      for(Integer neighbour : distances.keySet()){
        Integer distance = distances.get(neighbour);
        bw.write(neighbour + "\t" + distance+"\n");
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Creates a Map of nodes and their shortest path from {@code source}.
   * @param source Source node.
   * @return Map of nodes and their <shortest paths, distances>.
   * @throws Exception 
   */
  public Map<Integer, PathDistance> createShortestPathsForNode(int source) throws Exception {
    Set<Integer> nodes = new HashSet<Integer>(); // List of nodes reachable from {@code source}.
    Map<Integer,PathDistance> distances = Maps.newHashMap(); // List of distance from {@code source}.

    Queue<Integer> q1 = Queues.newLinkedBlockingQueue(KNeighbourhoodComputer.nodes.size());
    Queue<Integer> q2 = Queues.newLinkedBlockingQueue(KNeighbourhoodComputer.nodes.size());

    Set<Integer> visited = new HashSet<Integer>(); // List of visited nodes.

    q1.add(source);
    nodes.add(source);

    int level = 0;

    //visited.add(source);

    while ( !q1.isEmpty() && level<depth_level){
      q2.clear();
      while(!q1.isEmpty()){
        int front = q1.poll();
        List<Integer> neighbours = new ArrayList<Integer>(KNeighbourhoodComputer.graph.successors(front));
        for(Integer neighbour : neighbours){
          if(!nodes.contains(neighbour)){
            Integer neighbourDistance;
            List<Integer> path = Lists.newArrayList();
            if(distances.containsKey(front)){
              neighbourDistance = 1 + distances.get(front).distance; // Distance of neighbour is 1 + that of 'front'.
              for(int item:distances.get(front).path)
                path.add(item);
              path.add(front);
            }
            else{
              neighbourDistance = 1;
            }

            if(neighbourDistance>KNeighbourhoodComputer.depth_level){
              throw new Exception("Distance found to be greater than depth_val given.");
            }

            if(distances.containsKey(neighbour)){
              Integer oldDistance = distances.get(neighbour).distance;
              if(oldDistance > neighbourDistance){
                distances.put(neighbour, new PathDistance(path,neighbourDistance));
              }
            }
            else{
              distances.put(neighbour, new PathDistance(path,neighbourDistance));
            }
            
            nodes.add(neighbour);
            q2.add(neighbour);
          }
        }
        visited.add(front);
      }
      q1.clear();
      q1.addAll(q2);
      level++;
    }
    return distances;
  }

  /**
   * Creates a Map of nodes and their distances from {@code source}.
   * @param source Source node.
   * @return Map of nodes and their distances.
   * @throws Exception 
   */
  public Map<Integer, Integer> createShortestDistancesForNode(int source) throws Exception {
    Set<Integer> nodes = new HashSet<Integer>(); // List of nodes reachable from {@code source}.
    Map<Integer,Integer> distances = Maps.newHashMap(); // List of distance from {@code source}.

    Queue<Integer> q1 = Queues.newLinkedBlockingQueue(KNeighbourhoodComputer.nodes.size());
    Queue<Integer> q2 = Queues.newLinkedBlockingQueue(KNeighbourhoodComputer.nodes.size());

    Set<Integer> visited = new HashSet<Integer>(); // List of visited nodes.

    q1.add(source);
    nodes.add(source);

    int level = 0;

    //visited.add(source);

    while ( !q1.isEmpty() && level<depth_level){
      q2.clear();
      while(!q1.isEmpty()){
        int front = q1.poll();
        List<Integer> neighbours = new ArrayList<Integer>(KNeighbourhoodComputer.graph.successors(front));
        for(Integer neighbour : neighbours){
          if(!nodes.contains(neighbour)){
            Integer neighbourDistance;
            if(distances.containsKey(front))
              neighbourDistance = 1 + distances.get(front); // Distance of neighbour is 1 + that of 'front'.
            else
              neighbourDistance = 1;

            if(distances.containsKey(neighbour)){
              Integer oldDistance = distances.get(neighbour);
              if(oldDistance > neighbourDistance){
                if(neighbourDistance>KNeighbourhoodComputer.depth_level){
                  throw new Exception("Distance found to be greater than depth_val given.");
                }
                distances.put(neighbour, neighbourDistance);
              }
            }
            else{
              if(neighbourDistance>KNeighbourhoodComputer.depth_level){
                throw new Exception("Distance found to be greater than depth_val given.");
              }
              distances.put(neighbour, neighbourDistance);
            }

            nodes.add(neighbour);
            q2.add(neighbour);
          }
        }
        visited.add(front);
      }
      q1.clear();
      q1.addAll(q2);
      level++;
    }
    return distances;
  }

  private static ImmutableValueGraph<Integer, ArrayList<Integer>> readGraph() {
    System.out.println("Reading graph into memory----->");
    MutableValueGraph<Integer, ArrayList<Integer>> weightedGraph = ValueGraphBuilder.directed().allowsSelfLoops(true).build();

    BufferedReader br = null;

    try {
      br = new BufferedReader(new FileReader(Config.graphFileName));
      String line;

      while((line=br.readLine())!=null)
      {
        String[] nodes = line.split("\t");
        List<String> vals = Arrays.asList(nodes);
        if(vals.size()!=3) continue;
        Integer a = Integer.parseInt(vals.get(0));
        Integer b = Integer.parseInt(vals.get(2));
        Optional<ArrayList<Integer>> c1 =  weightedGraph.edgeValue(a, b);
        if(c1.isPresent())
        {
          ArrayList<Integer> c = (ArrayList<Integer>) c1.get();
          c.add(Integer.parseInt(vals.get(1)));
          weightedGraph.putEdgeValue(a, b, c);
        }
        else
        {
          ArrayList<Integer> c = new ArrayList<Integer>();
          c.add(Integer.parseInt(vals.get(1)));
          weightedGraph.putEdgeValue(a, b, c);
        }

        // Putting inverse edge.
        Optional<ArrayList<Integer>> c2 =  weightedGraph.edgeValue(b, a);
        if(c2.isPresent())
        {
          ArrayList<Integer> c = (ArrayList<Integer>) c2.get();
          c.add(Integer.parseInt(vals.get(1)));
          weightedGraph.putEdgeValue(b, a, c);
        }
        else
        {
          ArrayList<Integer> c = new ArrayList<Integer>();
          c.add(Integer.parseInt(vals.get(1)));
          weightedGraph.putEdgeValue(b, a, c);
        }
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
    System.out.println("Graph is in memory now!");
    return ImmutableValueGraph.copyOf(weightedGraph);
  }


  @SuppressWarnings("unused")
  private static Set<Integer> getSeenNodes(){
    System.out.println("Checking seen nodes----->");
    Set<Integer> seenNodes = new HashSet<Integer>();
    File folder = new File(Config.shortestPathFolderName);
    File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      seenNodes.add(Integer.parseInt(listOfFiles[i].getName()));
    }
    System.out.println("Got list of seen nodes.");
    return seenNodes;
  }

}

