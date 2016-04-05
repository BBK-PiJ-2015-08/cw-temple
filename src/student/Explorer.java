package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
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
         * GameState
         * getExit
         *
         * EscapeState
         * There is a method pickUpGold, but states you must first check if
         * there is gold. Need to find how to do this.
         * getTimeRemaining seems pretty key also
         *
         * Tile.java
         * Public method getOriginalGold() returns the original amount of gold
         * on this tile - so want to avoid already visited tiles as using
         * pickUpGold on a tile that we've taken all the gold from would throw
         * an IllegalStateException.
         */
        //Nodes that have been searched through
        PriorityQueueImpl<Node> openList = new PriorityQueueImpl<>();
        //Nodes that have not been fully searched
        PriorityQueueImpl<Node> closedList = new PriorityQueueImpl<>();
        openList.add(state.getCurrentNode(), 0); state.getCurrentNode();
        Collection<Node> theMap = state.getVertices();
        while (!openList.isEmpty()) {
            openList.poll();
        }
    }


}
