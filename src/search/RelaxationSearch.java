package search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import languagemodel.Document;
import languagemodel.LM;
import paths.KNeighbourhoodComputer;
import paths.SelectivePathComputer;
import utils.Config;
import utils.DBConnection;
/**
 * Class to fetch LM based relaxations for queried entity based on the paper
 * by Elbassouni et. al. (ESWC 2011).
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class RelaxationSearch {
  public static Map<Integer,LM> LMs = computeAllNodeLMs(); // All Node LMs
  int nodeID;

  public RelaxationSearch(String query) {
    String[] entities = query.trim().split(" ");
    String entity = entities[entities.length-1]; // Get relaxations of the latest entity.
    entity="<"+entity.toLowerCase()+">"; // toLowerCase() to ensure compatibility.
    if(SelectivePathComputer.nameToNum.containsKey(entity)){
      this.nodeID=SelectivePathComputer.nameToNum.get(entity);
    }
    else
      throw new IllegalArgumentException("Did not find existing id for queried node:"+entity);
  }

  private static Map<Integer, LM> computeAllNodeLMs() {
    List<Integer> nodes = new ArrayList<Integer>(KNeighbourhoodComputer.nodes);

    Map<Integer,LM> LMs = new HashMap<Integer,LM>();

    //Compute LM of each node
    System.out.println("Computing LM for each of the " + nodes.size() + " nodes-->");
    for(int node:nodes) {
      Document doc = new Document(node,false);
      doc.buildLM();
      LM nlm = doc.lm;
      LMs.put(node,nlm);
    }
    System.out.println("Done computing LM of nodes.");
    
    return LMs;
  }

  /**
   * Reads pre-computed LMs for nodes.
   * @return Map of Integer ID of node to its LM.
   */
  @SuppressWarnings("unused")
  private static Map<Integer,LM> readLMsFromFile() {
    System.out.println("Reading Node LMs from files-->");
    Map<Integer,LM> LMs = new HashMap<Integer,LM>();
    File folder = new File(Config.LMDir);
    File[] listOfFiles = folder.listFiles();
    
    for (int i = 0; i < listOfFiles.length; i++) {
      try {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(listOfFiles[i])); 
        // Method for serialization of object 
        LM lm = (LM)in.readObject(); 
        LMs.put(Integer.parseInt(listOfFiles[i].getName()), lm);
        in.close(); 
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } 
    }
    System.out.println("Done reading LMs from files.");
    return LMs;
  }

  public String getRelaxation() {
    //First check if the node is cached in DB.
    Connection conn = null;
    int lowestID = -1;
    try {
      conn = DBConnection.getConnection();

      Statement stmt = null;
      stmt = conn.createStatement();

      String sql = "SELECT relaxid FROM instasearch.relaxations WHERE id=" + nodeID;
      ResultSet rs = stmt.executeQuery(sql);
      while(rs.next()){
        lowestID = rs.getInt("relaxid");
      }
      rs.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    if(lowestID>0) {
      System.out.println("Found relaxation in DB.");
      return SelectivePathComputer.numToName.get(lowestID);
    }
    // If not found in DB, compute and cache in DB.
    double currentLowest=Double.MAX_VALUE;
    LM thisLM = RelaxationSearch.LMs.get(nodeID);
    for(Entry<Integer,LM> entry:RelaxationSearch.LMs.entrySet()) {
      if(entry.getKey()==nodeID)
        continue;
      LM otherLM = entry.getValue();
      double dist = thisLM.computeJSD(otherLM);
      if(dist<currentLowest) {
        currentLowest=dist;
        lowestID=entry.getKey();
      }
    }
    
    //Insert into DB for quick access next time.
    try {
      conn = DBConnection.getConnection();
      Statement stmt = null;
      stmt = conn.createStatement();
      String sql = "INSERT INTO instasearch.relaxations VALUES(" + nodeID + "," + lowestID +")";
      stmt.executeUpdate(sql);
      System.out.println("Inserted relaxation in DB.");
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return SelectivePathComputer.numToName.get(lowestID);
  }

}
