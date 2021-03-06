package student;

import game.Edge;
import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Jade Dickinson BBK-PiJ-2015-08
 */
public class Explorer {
    /**
     * Used in comparison between time remaining and cost to the exit.
     */
    private static final int TIMECOMPARISON = 1369;
    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     *
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     *
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these
     * tiles (ignoring walls and obstacles).
     *
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     *
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new
     * position.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        List<NodeStatus> visited = new ArrayList<>();
        greedy(state, visited, state.getCurrentLocation());
    }

    /**
     * Greedily move to the node in the current neighbours that is closest to
     * the target (the Orb).
     * @param state The EscapeState
     * @param visited Nodes already considered
     * @param startLocation Location of start point for this method call
     */
    private void greedy(ExplorationState state, List<NodeStatus> visited,
                       long startLocation) {
        if (state.getDistanceToTarget() == 0) {
            return;
        }
        long currentLocation = state.getCurrentLocation();
        Collection<NodeStatus> nbs = state.getNeighbours();
        List<NodeStatus> unsorted = new ArrayList<>();
        for (NodeStatus n : nbs) {
            unsorted.add(n);
        }
        Collections.sort(unsorted, (o1, o2) -> {
            if (o1.getDistanceToTarget() == o2.getDistanceToTarget()) {
                return 0;
            }
            return o1.getDistanceToTarget() < o2.getDistanceToTarget() ? -1 : 1;
        });
        Collection<NodeStatus> sortedNs = unsorted;
        for (NodeStatus nb : sortedNs) {
            if (!visited.contains(nb)) {
                visited.add(nb);
                if (state.getDistanceToTarget() != 0) {
                    state.moveTo(nb.getId());
                    greedy(state, visited, currentLocation);
                }
            }
        }
        if (visited.containsAll(sortedNs) && state.getDistanceToTarget() != 0) {
            state.moveTo(startLocation);
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect
     * as much gold as possible along the way. Your solution must ALWAYS escape
     * before time runs out, and this should be prioritized above collecting
     * gold.
     *
     * You now have access to the entire underlying graph, which can be accessed
     * through EscapeState. getCurrentNode() and getExit() will return you Node
     * objects of interest, and getVertices() will return a collection of all
     * nodes on the graph.
     *
     * Note that time is measured entirely in the number of steps taken, and for
     * each step the time remaining is decremented by the weight of the edge
     * taken. You can use getTimeRemaining() to get the time still remaining,
     * pickUpGold() to pick up any gold on your current tile (this will fail if
     * no such gold exists), and moveTo() to move to a destination node adjacent
     * to your current node.
     *
     * You must return from this function while standing at the exit. Failing to
     * do so before time runs out or returning from the wrong location will be
     * considered a failed run.
     *
     * You will always have enough time to escape using the shortest path from
     * the starting position to the exit, although this will not collect much
     * gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        Node startNode = state.getCurrentNode();
        final Node exitNode = state.getExit();
        final Collection<Node> theGraph = state.getVertices();
        seekGoldOrExit(state, theGraph, startNode, exitNode);
        return;
    }

    /**
     * seekGoldOrExit() goes for current highest gold tile, using dijkstra(),
     * if time remains to treasure hunt. It recursively calls seekGoldOrExit();
     * running out of time is the edge case and it can deal with this, as
     * described below:
     * When running out of time, seekGoldOrExit defaults to using dijkstra()
     * to find the exit. It also recursively calls seekGoldOrExit().
     *
     * @param state the EscapeState we're working with
     * @param theGraph the entire graph from using state.getVertices()
     * @param startNode the start point for this use of seekGoldOrExit
     * @param exitNode the final Node exitNode from escape()
     */
    private void seekGoldOrExit(EscapeState state,
                                final Collection<Node> theGraph,
                                Node startNode, Node exitNode) {
        if (state.getCurrentNode().equals(exitNode)) {
            return;
        }
        Node highest = null;
        int currentHighest = 0;
        for (Node n : theGraph) {
            if (n.getTile().getGold() > currentHighest) {
                highest = n;
                    currentHighest = n.getTile().getGold();
            }
        }
        int totalCosts = totalCosts(state.getCurrentNode(), highest,
                exitNode, state);
        //Time running out; move towards exit.
        if (state.getTimeRemaining() - TIMECOMPARISON < totalCosts) {
            final List<Node> escapeNow =
                    dijkstra(state.getCurrentNode(), exitNode, state);
            escapeNow.remove(0);
            for (int i = 0; i < escapeNow.size(); i++) {
                Node nxt = escapeNow.get(i);
                escapeNow.remove(nxt);
                if (state.getCurrentNode().equals(exitNode)) {
                    return;
                }
                if (state.getCurrentNode().getTile().getGold() > 0) {
                    state.pickUpGold();
                }
                state.moveTo(nxt);
                seekGoldOrExit(state, theGraph, nxt, exitNode);
            }
        } else {
            //Time remains to treasure hunt; move towards highest gold.
            List<Node> wayToHighest = dijkstra(startNode, highest, state);
            wayToHighest.remove(0);
            for (int i = 0; i < wayToHighest.size(); i++) {
                Node nxt = wayToHighest.get(i);
                wayToHighest.remove(nxt);
                if (state.getCurrentNode().getTile().getGold() > 0) {
                    state.pickUpGold();
                }
                state.moveTo(nxt);
                seekGoldOrExit(state, theGraph, nxt, exitNode);
            }
        }
    }

    /**
     * @param startNode the start point for this use of seekGoldOrExit
     * @param highest The Node with the current highest gold (or pizza)
     * @param exitNode The final Node exitNode from escape()
     * @param state The EscapeState we are working with
     * @return An integer representing the total cost of moving to the current
     * highest gold and then moving to the exit.
     */
    private int totalCosts(Node startNode,
                           Node highest, Node exitNode, EscapeState state) {
        List<Node> checkWayTarget = dijkstra(startNode, highest, state);
        List<Node> checkWayOut = dijkstra(highest, exitNode, state);
        if (!checkWayOut.isEmpty()) {
            checkWayOut.remove(0);
        }
        int costToTarget = 0;
        int costTargetToExit = 0;
        for (int i = 0; i + 1 < checkWayTarget.size(); i++) {
            Edge checkLength =
                    checkWayTarget.get(i).getEdge(checkWayTarget.get(i + 1));
            costToTarget = costToTarget + checkLength.length;
        }
        for (int i = 0; i + 1 < checkWayOut.size(); i++) {
            Edge checkLength =
                    checkWayOut.get(i).getEdge(checkWayOut.get(i + 1));
            costTargetToExit = costTargetToExit + checkLength.length;
        }
        return costToTarget + costTargetToExit;
    }

    /**
     * @param startNode The Node we are using dijkstra() to seek a path from.
     * @param end The Node we are using dijkstra() to seek a path to.
     * @param state The EscapeState we are working with
     * @return The path from startNode to end (our current target).
     */
    private List<Node> dijkstra(Node startNode, Node end, EscapeState state) {
        PriorityQueueImpl<Node> openList = new PriorityQueueImpl<>();
        HashMap<Node, NodeData> nodeData = new HashMap<>();
        openList.add(startNode, 0);
        nodeData.put(startNode, new NodeData());
        while (!openList.isEmpty() && openList.peek() != end) {
            Node currentNode = openList.poll();
            NodeData currentData = nodeData.get(currentNode);
            if (state.getCurrentNode().equals(state.getExit())) {
                return new ArrayList<>();
            } else {
                Set<Edge> escapeEdges = currentNode.getExits();
                for (Edge ed : escapeEdges) {
                    Node w = ed.getOther(currentNode);
                    NodeData wData = nodeData.get(w);
                    double wDistance = currentData.distance + ed.length;
                    if (wData == null) {
                        openList.add(w, wDistance);
                        nodeData.put(w, new NodeData(currentNode, wDistance));
                    } else {
                        if (wDistance < wData.distance) {
                            openList.updatePriority(w, wDistance);
                            wData.distance = wDistance;
                            wData.prev = currentNode;
                        }
                    }
                }
            }
        }
        if (openList.isEmpty()) {
            while (!state.getCurrentNode().equals(state.getExit())) {
                visitAnother(state);
            }
            //Return a dummy list, only used to return out of this method.
            return new ArrayList<>();
        } else {
            return findWayOut(openList.peek(), nodeData);
        }
    }

    /**
     * Used to handle some extreme edge cases; just moves to a random node.
     * Called recursively by dijkstra() while current node isn't equal to exit.
     *
     * @param state The EscapeState we're working with.
     */
    private void visitAnother(EscapeState state) {
        Set<Node> edgesOut = state.getCurrentNode().getNeighbours();
        if (edgesOut.contains(state.getExit())) {
            if (state.getCurrentNode().getTile().getGold() > 0) {
                state.pickUpGold();
            }
            state.moveTo(state.getExit());
        } else {
            for (Node e : edgesOut) {
                if (state.getCurrentNode().getTile().getGold() > 0) {
                    state.pickUpGold();
                }
                state.moveTo(e);
            }
        }
    }

    /**
     * @param end The end of the path this method returns to dijkstra()
     * @param nodeData Must contain information about the path
     * @return The path from current node to target node (end or highest gold).
     */
    private List<Node> findWayOut(Node end, HashMap<Node, NodeData> nodeData) {
        List<Node> wayOut = new ArrayList<>();
        Node n = end;
        while (n != null) {
            wayOut.add(n);
            n = nodeData.get(n).prev;
        }
        Collections.reverse(wayOut);
        return wayOut;
    }

    /**
     * A NodeData object holds information about a node, that being the previous
     * node on a path to it, and the distance from the start node in the path to
     * this node.
     */
    private static final class NodeData {
        /**
         * prev Holds the previous node on a path to this node.
         */
        private Node prev;
        /**
         * Distance holds distance in path from start node to this node.
         */
        private double distance;

        /**
         * @param n The previous node.
         * @param dist The distance from the previous to this node.
         */
        private NodeData(Node n, double dist) {
            this.prev = n;
            this.distance = dist;
        }

        /**
         * Default constructor with no parameters.
         */
        private NodeData() {
        }
    }

}
