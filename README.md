### PiJ Coursework 4 - George Osborne and the Temple of Gloom
Did not work as part of a pair.
As an aside, please try watching him run the maze at top speed while listening
to "Flight of the Bumblebee". You will not regret this.

Additional methods in Explorer.java
public void greedy (ExplorationState state, List<NodeStatus> visited, long startLocation)
private void seekGoldOrExit(EscapeState state, Collection<Node> theGraph, Node startNode, Node exitNode)
private List<Node> dijkstra(Node startNode, Node exitNode)
private List<Node> findWayOut(Node end, HashMap<Node, NodeData> nodeData)

Explorer.java contains a nested inner class, NodeData. For a given Node, a
NodeData object holds information about the previous node on a path to this Node
and the distance from the start node in the path to this node.