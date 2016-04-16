package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
     * A suggested first implementation that will always find the orb, but
     * likely won't receive a large bonus multiplier, is a depth-first search.
     *
     * JD: thoughts on possible algorithms
     * Depth-first (Stack)
     * Breadth-first (Queue)
     * Best first (Priority Queue) <- there is one in this project.
     * A* (also uses a Priority Queue)
     * Dijkstra's algorithm (also uses a Priority Queue)
     *
     * @param state the information available at the current state
     */

    private Integer costSoFar = 0;

    public void explore(ExplorationState state) {
        List<NodeStatus> visited = new ArrayList<NodeStatus>();
        greedy(state, visited, state.getCurrentLocation());
    }

    public void depthFirst (ExplorationState state, List<NodeStatus> visited, long startLocation) {
        int distance = Integer.MAX_VALUE;
        long currentLocation = state.getCurrentLocation();
        Collection<NodeStatus> nbs = state.getNeighbours();
        for (NodeStatus nb : nbs) {
            if (!visited.contains(nb)) {
                visited.add(nb);
                state.moveTo(nb.getId());
                if (state.getDistanceToTarget() == 0) {
                    System.out.println("You have found the orb!");
                    break;
                }
                depthFirst(state, visited, currentLocation);
            }
        }
        if (visited.containsAll(nbs)) {
            state.moveTo(startLocation);
        }
    }

    public void greedy (ExplorationState state, List<NodeStatus> visited, long startLocation) {
        //Best-case: 1.3 bonus multiplier. Worst-case: 1.0.
        if (state.getDistanceToTarget() == 0) {
            System.out.println("You have found the orb!");
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
     * Thoughts on possibilities:
     * Dijkstra's algorithm (also uses a Priority Queue)
     * A* ("") <- extension of Dijkstra's which uses heuristics to guide search.
     * Going to start with this instead of best-first based on
     * http://theory.stanford.edu/~amitp/GameProgramming/AStarComparison.html
     * Best first (Priority Queue) <- there is one in this project.
     *
     * @param state the information available at the current state
     */
    /**
     * useful:
     * Cavern
     * getRowCount()
     * getColumnCount()
     * getGraph > getVertices line 296 GameState
     * getTarget
     * getTileAt
     * getNodeAt > line 150 in GameState
     * (private) minPathLengthToTarget: implementation of Dijkstra's algorithm that returns
     * only the minimum distance between the given node and the target node for
     * this cavern (no path).
     *
     * Checking for gold/pizza
     * getVertices to get the entire graph
     * for each node getTile
     * for the tile getGold to get amount of gold
     * sort nodes by gold amount
     *
     */
    /** Previous attempt to do A* (have branched off)
     //Nodes that have been searched through
     PriorityQueueImpl<Node> openList = new PriorityQueueImpl<>();
     //Nodes that have not been fully searched
     PriorityQueueImpl<Node> closedList = new PriorityQueueImpl<>();
     openList.add(state.getCurrentNode(), 0); state.getCurrentNode();

     Node startNode = state.getCurrentNode();
     Node exitNode = state.getExit();
     while (!openList.isEmpty()) {
     Node currentNode = openList.poll();

     if (currentNode.equals(exitNode)) {
     System.out.println("You have reached the exit!");
     return;
     }
     // Node - public methods:
     // long getID()
     // Edge getEdge(Node q)
     // Set<Edge> getExits()
     // Set<Node> getNeighbours()
     // Tile getTile()
     // boolean equals(Object ob)
     // int hashCode
     Collection<Node> neighbours = exitNode.getNeighbours();
     for (Node n : neighbours) {
     if (n.equals(exitNode)) {

     }
     // Cost, f, of a node given by f = g + h
     // g = cost from start to node (number of tiles moved so far)
     // h = heuristic estimating cheapest path to goal


     }

     //not sure what double p should be in following line (have put 0):
     closedList.add(currentNode, 0);
     }
     */
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
        System.out.println("George has " + state.getTimeRemaining() + " steps left before ceiling collapses");
        //Number of steps remaining varies wildly at start of escape phase.
        /**
         * timeRemaining based on computeTimeToEscape, from
         * escapeCavern.minPathLengthToTarget(position), that method is a
         * package-private implementation to Dijkstra's algorithm returning the
         * minimum distance between the current node and the target node (exit).
         */
        Node startNode = state.getCurrentNode();
        Node exitNode = state.getExit();
        PriorityQueueImpl<Node> openList = new PriorityQueueImpl<>();
        PriorityQueueImpl<Node> closedList = new PriorityQueueImpl<>();
        openList.add(startNode, 0);

        while (!openList.isEmpty()) {
            //Note scope for currentNode and currentNeighbours
            Node currentNode = openList.poll();
            if (currentNode.equals(exitNode)) {
                System.out.println("You have escaped!");
                return;
            }
            //Concerned the priority queue is actually able to handle sorting by
            //distance so the below might be redundant
            Collection<Node> escapeNbs = currentNode.getNeighbours();
            List<Node> willBeSorted = new ArrayList<>();
            for (Node e : escapeNbs) {
                willBeSorted.add(e);
            }
            Collections.sort(willBeSorted, new Comparator<Node>(){
                public int compare(Node o1, Node o2){
                    if(getDistanceToNeighbour(currentNode, o1) == getDistanceToNeighbour(currentNode, o2))
                        return 0;
                    return getDistanceToNeighbour(currentNode, o1) < getDistanceToNeighbour(currentNode, o2) ? -1 : 1;
                }
            });
            HashMap<Node, Integer> currentNeighbours = new HashMap<Node, Integer>();
            for (Node w : willBeSorted) {
                //from GameState moveTo(Node n), distance=length between 2 nodes
                Integer thisEdgeWeight = getDistanceToNeighbour(currentNode, w);
                System.out.println("Edge weight between current and this" +
                        " neighbour is: " + thisEdgeWeight);

                currentNeighbours.put(w, thisEdgeWeight);
            }

        }
        //NB private goldPickedUp is false if gold hasn't been picked up
    }

    private int getDistanceToNeighbour(Node currentNode, Node neighbour) {
        int distanceBetweenNodes = currentNode.getEdge(neighbour).length();
        return distanceBetweenNodes;
    }

    private Integer getCostSoFar(Node currentNode, Node neighbour) {
        costSoFar = costSoFar + getDistanceToNeighbour(currentNode, neighbour);
        return costSoFar;
    }

}
