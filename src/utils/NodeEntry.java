package utils;

public class NodeEntry {
  public int nodeID;
  public int distanceFromSrc;

  public NodeEntry(int nodeID, int distanceFromSrc){
    this.nodeID = nodeID;
    this.distanceFromSrc = distanceFromSrc;
  }

  public int getDistance() {
    return this.distanceFromSrc;
  }

  @Override
  public String toString() {
    return "NodeEntry [nodeID=" + nodeID + ", distanceFromSrc=" + distanceFromSrc + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + distanceFromSrc;
    result = prime * result + nodeID;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodeEntry other = (NodeEntry) obj;
    if (distanceFromSrc != other.distanceFromSrc)
      return false;
    if (nodeID != other.nodeID)
      return false;
    return true;
  }
}
