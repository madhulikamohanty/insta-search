package search.utils;
import java.util.Comparator;

public class TreeResultComparator implements Comparator<MyTreeResult> {

  @Override
  public int compare(MyTreeResult a1, MyTreeResult a2) {
    if (a1.getNumEdges()<a2.getNumEdges())
    {
      return -1;
    }
    if (a1.getNumEdges()>a2.getNumEdges())
    {
      return 1;
    }
    return 0;
  }

}