package student;

import game.Edge;
import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Jade Dickinson BBK-PiJ-2015-08
 */
public class Explorer {
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
        List<NodeStatus> visited = new ArrayList<NodeStatus>();
        greedy(state, visited, state.getCurrentLocation());
    }

    public void greedy (ExplorationState state, List<NodeStatus> visited, long startLocation) {
        if (state.getDistanceToTarget() == 0) {
            return;
        }
        long currentLocation = state.getCurrentLocation();
        Collection<NodeStatus> nbs = state.getNeighbours();
        List<NodeStatus> unsorted = new ArrayList<>();
        for (NodeStatus n : nbs) {
            unsorted.add(n);
        }
        Collections.sort(unsorted, new Comparator<NodeStatus>(){
            public int compare(NodeStatus o1, NodeStatus o2){
                if(o1.getDistanceToTarget() == o2.getDistanceToTarget())
                    return 0;
                return o1.getDistanceToTarget() < o2.getDistanceToTarget() ? -1 : 1;
            }
        });
        Collection<NodeStatus> sortedNbs = unsorted;
        for (NodeStatus nb : sortedNbs) {
            if (!visited.contains(nb)) {
                visited.add(nb);
                if (state.getDistanceToTarget() != 0) {
                    state.moveTo(nb.getId());
                    greedy(state, visited, currentLocation);
                }
            }
        }
        if (visited.containsAll(sortedNbs) && state.getDistanceToTarget() != 0) {
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
        Collection<Node> theGraph = state.getVertices();
        seekGoldOrExit(state, theGraph, startNode, exitNode);
        return;
    }

    private void seekGoldOrExit(EscapeState state, Collection<Node> theGraph, Node startNode, Node exitNode) {
        if (state.getCurrentNode().equals(exitNode)) {
            return;
        }
        List<Node> checkWayOut = dijkstra(startNode, exitNode);
        int sumOfCosts = 0;
        for (int i = 0; i+1 < checkWayOut.size(); i++) {
            Edge checkLength = checkWayOut.get(i).getEdge(checkWayOut.get(i+1));
            sumOfCosts = sumOfCosts + checkLength.length;
        }
        if (state.getTimeRemaining() - 1016 < sumOfCosts) {
            if (state.getCurrentNode().equals(exitNode)) {
                return;
            }
            List<Node> escapeNow = dijkstra(state.getCurrentNode(), exitNode);
            escapeNow.remove(0);
            for (Node f : escapeNow) {
                state.moveTo(f);
                if (state.getCurrentNode().equals(exitNode)) {
                    return;
                }
                if (f.getTile().getGold() > 0) {
                    state.pickUpGold();
                }
            }
        }
        else {
            Node highestOrNull = null;
            int currentHighest = 0;
            for (Node n : theGraph) {
                if (n.getTile().getGold() > currentHighest) {
                    highestOrNull = n;
                    currentHighest = n.getTile().getGold();
                }
            }
            while (currentHighest > 0) {
                List<Node> wayToHighest = dijkstra(startNode, highestOrNull);
                wayToHighest.remove(0);
                for (int i = 0; i < wayToHighest.size(); i++) {
                    if (state.getCurrentNode().equals(exitNode)) {
                        return;
                    }
                    Node f = wayToHighest.get(i);
                    wayToHighest.remove(f);
                    state.moveTo(f);
                    if (state.getCurrentNode().equals(exitNode)) {
                        return;
                    }
                    else {
                        if (f.getTile().getGold() > 0) {
                            state.pickUpGold();
                        }
                        theGraph = state.getVertices();
                        seekGoldOrExit(state, theGraph, f, exitNode);
                    }
                }
            }
        }
    }

    private List<Node> dijkstra(Node startNode, Node exitNode) {
        PriorityQueueImpl<Node> openList = new PriorityQueueImpl<>();
        HashMap<Node, totalCost> totalCost = new HashMap<Node, totalCost>();
        openList.add(startNode, 0);
        totalCost.put(startNode, new totalCost());
        while (!openList.isEmpty() && openList.peek() != exitNode) {
            Node currentNode = openList.poll();
            totalCost currentCost = totalCost.get(currentNode);
            Set<Edge> escapeEdges = currentNode.getExits();
            for (Edge ed : escapeEdges) {
                Node w = ed.getOther(currentNode);
                totalCost wCost = totalCost.get(w);
                double wDistance = currentCost.distance + ed.length;
                if (wCost == null) {
                    openList.add(w, wDistance);
                    totalCost.put(w, new totalCost(currentNode, wDistance));
                }
                else {
                    if (wDistance < wCost.distance) {
                        openList.updatePriority(w, wDistance);
                        wCost.distance = wDistance;
                        wCost.prev = currentNode;
                    }
                }
            }
        }
        return findWayOut(openList.peek(), totalCost);
    }

    private List<Node> findWayOut(Node end, HashMap<Node, totalCost> totalCost) {
        List<Node> wayOut = new ArrayList<Node>();
        Node n = end;
        while (n != null) {
            wayOut.add(n);
            n = totalCost.get(n).prev;
        }
        Collections.reverse(wayOut);
        return wayOut;
    }

    private static class totalCost {
        private Node prev;
        private double distance;
        private totalCost(Node n, double dist) {
            prev = n;
            distance = dist;
        }
        private totalCost() {
        }
    }

}
