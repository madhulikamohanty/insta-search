package search;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import paths.SelectivePathComputer;
import search.utils.EdgeNode;
import search.utils.MyTreeResult;
import search.utils.Node;

public class TreeResultToJSONFile {

  public static void saveToFile(List<MyTreeResult> res, String filename) {
    try{
      FileWriter fw = new FileWriter(filename);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write("[\n");
      boolean isFirst = true;
      for(MyTreeResult rr:res) {
        if(!isFirst)
          bw.write(",\n");
        EdgeNode eRoot=new EdgeNode(rr.getRoot(),0); //TODO: changed to 0 from "null". Follow up?
        JSONObject JSONobj=constructJSON(rr.getResult(),eRoot);
        bw.write(JSONobj.toJSONString()+"\n");
        isFirst=false;
      }
      bw.write("]");
      bw.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked") //TODO Unchecked JSONObjects.
  public static JSONObject constructJSON(HashMap<Node,ArrayList<EdgeNode>> rr, EdgeNode n){
    if (rr.size()==0)
      return null;
    JSONObject JSONobj = new JSONObject();
    JSONArray children = new JSONArray();
    ArrayList<EdgeNode> en=rr.get(n.getNode());

    if(en!=null)
      for(int i=0;i<en.size();i++){
        EdgeNode enTmp=en.get(i);
        JSONObject ret=constructJSON(rr,enTmp);
        if(ret!=null)
          children.add(ret);
      }


    JSONobj.put("children", children);
    JSONobj.put("EntityName", SelectivePathComputer.numToName.get(n.getNode().getId()).toString());
    JSONobj.put("relationshipTo",SelectivePathComputer.numToName.get(n.getAnnotation()));
    JSONobj.put("isKeywordNode", true);
    JSONArray VAL = new JSONArray();
    String temp="";
    VAL.add(temp);
    JSONobj.put("values", VAL);

    return JSONobj;

  }
}
