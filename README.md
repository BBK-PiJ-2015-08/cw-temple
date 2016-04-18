### PiJ Coursework 4 - George Osborne and the Temple of Gloom
Jade Dickinson : BBK-PiJ-2015-08 : jdicki04

Did not work as part of a pair.

As an aside, please try watching him run the maze at top speed while listening
to "Flight of the Bumblebee". You will not regret this.

Additional methods in Explorer.java
* greedy() - Greedily move to the node in the current neighbours that is closest to the target - the Orb.
* seekGoldOrExit() - Move to current highest gold tile, using dijkstra(), repeatedly, if time remains to treasure hunt. When running out of time (edge case), seekGoldOrExit() defaults to using dijkstra() to find the exit.
* totalCosts() - Returns an integer representing the total cost of first moving to the current highest gold and then moving to the exit.
* dijkstra() - Returns the path from startNode to end (our current target).
* visitAnother() - Used to handle some extreme edge cases; just moves to a random node. Called recursively by dijkstra() while current node isn't equal to exit.
* findWayOut() - Returns the path from current node to target node (end or highest gold).

Explorer.java contains a nested inner class, NodeData. For a given Node, a NodeData object holds information about the previous node on a path to this Node and the distance from the start node in the path to this node.