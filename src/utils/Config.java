package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Class having values for all the configuration parameters. 
 * The properties are read from {@value #configFile} file.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class Config {
  public static String catalinaBase = "";
  public static final String configFile = catalinaBase + "conf/config.properties";
  public static String queryFile;
  public static String nodesMapFile;
  public static String nodesUnigramFile;
  public static String nodesBigramFile;
  public static String relationSubjectFile;
  public static String relationObjectFile;
  public static String relationBigramFile;
  public static String resultsDir = catalinaBase + "results/";
  public static String instaresultsFile = "instaresults.json";
  
  
  public static String INDEX_DIR;
  public static String DATA_DIR;
  public static int numThreads;
  
  public static String graphFileName;
  public static String shortestPathFolderName;
  public static String shortestDistanceFolderName;
  public static String reachabilityFolderName;
  
  /** Database Info. */
  public static String dbConfigFile = catalinaBase + "conf/instasearch-db.properties";
  public static String dataTableName = "instasearch.yagofacts";
  
  /**
   * Created using: create table instasearch.nodes as select distinct subject 
   * from instasearch.yagofacts union select distinct object from instasearch.yagofacts ;
   */
  public static String nodesTableName = "instasearch.nodes";
  
  static{
    Config.loadProperties();
  }
  
  public static void loadProperties(){
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(new File(configFile)));
      Config.INDEX_DIR = catalinaBase + props.getProperty("INDEX_DIR");
      Config.DATA_DIR = catalinaBase + props.getProperty("DATA_DIR");
      Config.numThreads = Integer.parseInt(props.getProperty("numThreads","2"));
      Config.graphFileName = catalinaBase + props.getProperty("graphFileName");
      Config.shortestPathFolderName = catalinaBase + props.getProperty("shortestPathFolderName");
      Config.shortestDistanceFolderName = catalinaBase + props.getProperty("shortestDistanceFolderName","shortestDistances/");
      Config.reachabilityFolderName = catalinaBase + props.getProperty("reachabilityFolderName","reachabilityInfo/");
      Config.queryFile = catalinaBase + props.getProperty("queryFile","queries.txt");
      Config.nodesMapFile = catalinaBase + props.getProperty("nodesMapFile","MapOfNum.tsv");
      Config.nodesUnigramFile = catalinaBase + props.getProperty("nodesUnigramFile","nodeUnigrams.tsv");
      Config.nodesBigramFile = catalinaBase + props.getProperty("nodesBigramFile","nodeBigrams.tsv");
      Config.relationSubjectFile = catalinaBase + props.getProperty("relationSubjectFile","relationSubjectUnigrams.tsv");
      Config.relationObjectFile = catalinaBase + props.getProperty("relationObjectFile","relationObjectUnigrams.tsv");
      Config.relationBigramFile = catalinaBase + props.getProperty("relationBigramFile","relationBigrams.tsv");
    } catch (FileNotFoundException e) {
      System.out.println("File path:"+new File(configFile).getAbsolutePath());
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void printProperties() {
  
  }
}
