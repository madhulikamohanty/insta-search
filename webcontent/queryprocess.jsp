<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="search.SearchGraph"%>
<%@page import="utils.Config"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Query processing file</title>
</head>
<body>
<% 
String clientOrigin = request.getHeader("origin");
response.setHeader("Access-Control-Allow-Origin", clientOrigin);
response.setHeader("Access-Control-Allow-Methods", "POST");
response.setHeader("Access-Control-Allow-Headers", "Content-Type");
response.setHeader("Access-Control-Max-Age", "86400");
System.out.println("Received request.");
SearchGraph sg = new SearchGraph();
String query = request.getParameter("query");
System.out.println("Insta-search Query:"+query);
String answers = sg.processQuery(query.trim(),2);
out.println(answers.replace("<","&lt").replace(">","&gt").replace("\n","<br>"));
out.println("<iframe src=\"MyAns.html?jsonFilename=results/"+Config.instaresultsFile+"\" width=\"100%\" height=\"800\"></iframe>");
System.out.println("Processed request.");
%>

</body>
</html>
