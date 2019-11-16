package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * A class for database configuration parameters.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class DBConfig {
  String serverName;
  String dbName;
  String username;
  String password;
  public DBConfig(String configFile){
    Properties props = new Properties();
    FileInputStream in;
    try {
      in = new FileInputStream(configFile);
      props.load(in);
      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    serverName = props.getProperty("dataSource.serverName");
    dbName = props.getProperty("dataSource.databaseName");
    username = props.getProperty("dataSource.user");
    password = props.getProperty("dataSource.password");
  }
}
