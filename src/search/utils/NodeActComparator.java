package search.utils;
import java.util.Comparator;

public class NodeActComparator implements Comparator<NodeActVals> {

  @Override
  public int compare(NodeActVals a1, NodeActVals a2) {
    if (a1.getActivationval()>a2.getActivationval())
        {
            return -1;
        }
        if (a1.getActivationval()<a2.getActivationval())
        {
            return 1;
        }
    return 0;
  }

}
