package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.*;

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
     * tile by its ID. Doing this will change state to reflect your new position.
     *
     * A suggested first implementation that will always find the orb, but
     * likely won't receive a large bonus multiplier, is a depth-first search.
     *
     * JD: thoughts on possible algorithms
     * Depth-first (Stack)
     * Breadth-first (Queue)
     * Best first (Priority Queue) <- there is one in this project.
     * A* (also uses a Priority Queue)
     * Dijkstra's algorithm
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        /**
         * ExplorationState has (implemented in GameState):
         * long getCurrentLocation - returns position.getId
         *      position = exploreCavern.getEntrance
         *      exploreCavern = Cavern.digExploreCavern(ROWS, COLS, rand);
         */
        //getNeighbours is in GameState
        List<NodeStatus> visited = new ArrayList<NodeStatus>();
        long startLocation = state.getCurrentLocation();
        greedy(state, visited, startLocation);
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
        long currentBestDist = Integer.MAX_VALUE;
        long bestID = -1L;
        long bestIDX = state.getCurrentLocation();
        long currentLocation = state.getCurrentLocation();
        while (state.getDistanceToTarget() != 0) {
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
                for (NodeStatus neb : sortedNbs) {
                    if (neb.getDistanceToTarget() < currentBestDist) {
                        currentBestDist = neb.getDistanceToTarget();
                        bestID = neb.getId();
                    }
                }
                if (!visited.contains(nb) && nb.getId() == bestID) {
                    visited.add(nb);
                    state.moveTo(bestID);
                    startLocation = state.getCurrentLocation();
                    greedy(state, visited, currentLocation);
                }
            }
            if (visited.containsAll(nbs)) {
                state.moveTo(currentLocation);
                //greedy(state, visited, startLocation);
            }
        }
        System.out.println("You have found the orb!");
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
    }


}
