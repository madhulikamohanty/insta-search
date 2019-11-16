<%@ page language="java" contentType="application/json; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="autocomplete.Autocomplete"%>
<%@page import="com.fasterxml.jackson.core.JsonProcessingException"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>


<%

  //System.out.println("Hello! I am entering autocomplete.jsp.");
  //response.setHeader("Content-Type", "");
  Autocomplete ac = new Autocomplete();

  String query = request.getParameter("term");
  System.out.println("Query:"+query);
  String[] keywords = query.split(" ");
  query = keywords[keywords.length-1];
  System.out.println("Query_filtered:"+query);
  List<String> suggestions = ac.getSuggestions(query);

  String json = null;

  try {
    json = new ObjectMapper().writeValueAsString(suggestions);
  } catch (JsonProcessingException ex) {
    ex.printStackTrace();
  }
  out.println(json);
  System.out.println(json);
  //System.out.println("Hello! I finished autocomplete.jsp."+json);
  //Iterator<String> iterator = suggestions.iterator();
 // while (iterator.hasNext()) {
  //  String term = (String) iterator.next();
 //   out.println(term);
 // }
%>
