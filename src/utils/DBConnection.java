package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A class for managing database connections to Postgres.
 *
 *
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class DBConnection {

  private static DBConfig dbc = new DBConfig(Config.dbConfigFile);
  private static final String JDBC_DRIVER = "org.postgresql.Driver"; 

  public static Connection getConnection() throws SQLException {
    Connection conn=null;
    try{
      Class.forName(JDBC_DRIVER);
      String url = "jdbc:postgresql://"+dbc.serverName+"/"+dbc.dbName;
      conn=DriverManager.getConnection(url, dbc.username,dbc.password);
    }catch(Exception e){
      e.printStackTrace();
    }
    return conn;
  }
}
