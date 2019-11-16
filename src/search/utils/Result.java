package search.utils;
import java.util.ArrayList;

import languagemodel.Triple;



public class Result {

  ArrayList<Triple> result;

  public Result(){
    this.result=new ArrayList<Triple>();
  }
  public void addTriple(Triple t){
    result.add(t);
  }

  public String toString(){
    String retval="";
    for(int i=0;i<result.size();i++){
      Triple t=result.get(i);
      retval+=t.getSubject().toString()+"-"+t.getObject().toString()+"->";
    }
    return retval;
  }

  public int size() {
    return this.result.size();
  }
  public ArrayList<Triple> getResult(){
    return this.result;
  }
}
