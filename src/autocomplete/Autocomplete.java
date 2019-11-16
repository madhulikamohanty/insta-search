package autocomplete;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import utils.DBConnection;

/**
 * Class to enable autocompletion of entities while the user is typing.
 * 
 * @author Madhulika Mohanty (madhulikam@cse.iitd.ac.in)
 *
 */
public class Autocomplete {

  /**
   * Function to fetch suggestions from list of nodes.
   * 
   * @param query Partial query typed by the user.
   * @return {@link ArrayList} of autocomplete suggestions.
   */
  public List<String> getSuggestions(String query) {
    query = query.toLowerCase();
    Connection conn = null;
    List<String> matched = null;
    try {
      conn = DBConnection.getConnection();

      Statement stmt = null;
      matched = new ArrayList<String>();
      stmt = conn.createStatement();

      //Doing only prefix matching. Requires "create index nodelower_index on instasearch.nodes(lower(node) text_pattern_ops);" for quick execution.
      String sql = "SELECT node FROM instasearch.nodes WHERE lower(node) LIKE '" + query +"%' ORDER BY id LIMIT 9";
      ResultSet rs = stmt.executeQuery(sql);
      while(rs.next()){
        String term = rs.getString("node");
        
        /* Remove beginning and end "<" and ">" tags.
         * NOT REQUIRED anymore
        StringBuilder sb = new StringBuilder(term);
        sb.deleteCharAt(term.length() - 1);
        sb.deleteCharAt(0);
        term=sb.toString();*/
        
        matched.add(term);
      }

      rs.close();
      conn.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return matched;
  }
}
