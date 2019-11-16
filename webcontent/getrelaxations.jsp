<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="search.RelaxationSearch"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Relaxations Fetcher</title>
</head>
<body>
<% 
String clientOrigin = request.getHeader("origin");
response.setHeader("Access-Control-Allow-Origin", clientOrigin);
response.setHeader("Access-Control-Allow-Methods", "POST");
response.setHeader("Access-Control-Allow-Headers", "Content-Type");
response.setHeader("Access-Control-Max-Age", "86400");
String query = request.getParameter("query");
System.out.println("Received relaxations fetch request for:"+query);
RelaxationSearch rs = new RelaxationSearch(query);
String relaxation = rs.getRelaxation();
relaxation=relaxation.replace("<","").replace(">","");
out.println("Why not try:  "+relaxation);
System.out.println("Processed fetch relaxation request:"+relaxation);
%>
</body>
</html>