<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="search.SearchAndCluster"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>LM Clustered Keyword Search Results</title>
</head>
<body>
	<%
	  String clientOrigin = request.getHeader("origin");
	  response.setHeader("Access-Control-Allow-Origin", clientOrigin);
	  response.setHeader("Access-Control-Allow-Methods", "POST");
	  response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	  response.setHeader("Access-Control-Max-Age", "86400");
	  System.out.println("Received Bidir request.");
	  String query = request.getParameter("query");
	  System.out.println("Bidir Query:" + query);
	  SearchAndCluster sac = new SearchAndCluster(query, 15);
	  int numOfClusters = sac.doSearchAndCluster();
	  // Read cluster files and print.
	  for (int i = 1; i <= numOfClusters; i++) {
	    out.println("<h3>Cluster-"+i+"</h3>");
	    out.println("<iframe src=\"MyAns.html?jsonFilename=results/" + query.replace(" ", ",") + "-" + i
	        + ".json\" width=\"100%\" height=\"400\"></iframe>");
	    out.println("<br><br>");
	  }
	  System.out.println("Processed Bidir request.");
	%>

</body>
</html>