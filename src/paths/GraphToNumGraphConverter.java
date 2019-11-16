package paths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GraphToNumGraphConverter {

  public static String graphFileName = "yagoFacts.tsv";
  public static Map<String,Long> numToNode;
  public static void main(String[] args) {
    numToNode = Maps.newHashMap();
    Long count = new Long(1);
    String line;
    BufferedReader br = null;
    BufferedWriter wr = null;
    System.out.println("Starting to read and write to file.");
    try {
      br = new BufferedReader(new FileReader(graphFileName));
      wr = new BufferedWriter(new FileWriter("num-"+graphFileName));
      line = br.readLine(); //Description line of file. Do Nothing.
      while ((line = br.readLine()) != null) {
        line.trim();
        String[] nodes = line.split("\t");
        List<String> vals = Arrays.asList(nodes).subList(1, nodes.length);
        List<Long> toWrite = Lists.newArrayList();
        for(String node: vals){
          //System.out.println(node);
          if(!numToNode.containsKey(node)){
            numToNode.put(node, count);
            count++;
          }
          toWrite.add(numToNode.get(node));
        }
        wr.write(Joiner.on("\t").join(toWrite)+"\n");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }finally {
      try {
        wr.close();
        if (br != null)
          br.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    System.out.println("Done reading and write to file.");
    
    //Write the map to file.
    System.out.println("Starting to write the map to file.");
    try {
      wr = new BufferedWriter(new FileWriter("MapOfNum.tsv"));
      for(Entry<String, Long> e:numToNode.entrySet()){
        wr.write(e.getValue()+"\t"+e.getKey()+"\n");
      }
      wr.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    System.out.println("Done with writing the map to file.");
  }
}
