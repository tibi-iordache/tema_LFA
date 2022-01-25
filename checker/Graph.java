import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    int nodesCount;
    List<Integer> adjLists[];

    Graph(int nodesCount) {
        this.nodesCount = nodesCount;

        this.adjLists = new List[nodesCount];

        for (int i = 0; i < nodesCount; ++i)
            adjLists[i] = new ArrayList<>();
    }

    void addEdge(int a, int b) {
        adjLists[a].add(b);
    }

    /** Basic depth first search in the graph */
    void DFS(int currentNode, boolean visited[]) {
        // mark the current node as visited
        visited[currentNode] = true;

        // check all its neighbors recursively
        for (int neighbor : adjLists[currentNode]) {
            if (!visited[neighbor])
                DFS(neighbor, visited);
        }
    }

    boolean isFinalState(int node, List<Integer> finalStates) {
        for (int finalState : finalStates)
            if (node == finalState) return true;

        return false;
    }

    /**
     * Breath first search in the graph until we find a state that is final.
     *
     * Returns true if it did find a final state, false otherwise.
     * */
    boolean BFS(int startNode, List<Integer> finalStates) {
        // case for when the start node itself is a final node
        if (isFinalState(startNode, finalStates)) return true;

        boolean visited[] = new boolean[nodesCount];
        LinkedList<Integer> queue = new LinkedList<>();

        // mark the current node as visited and save it in the queue
        visited[startNode]=true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            // extract a node from the queue
            int currentNode = queue.poll();

            // check all its neighbors
            for (int neighbor : adjLists[currentNode]) {
                if (!visited[neighbor]) {
                    if (isFinalState(neighbor, finalStates))
                        return true;

                    // mark the neighbor as visited and save it in the queue
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }

    /**
     * Computes the new states by applying all the symbols from the
     * sequence list given as param to the oldStates and then removing
     * the duplicates.
     * */
    List<Integer> computeNewStatesSet(List<Integer> oldStates,
                                      List<Integer> sequence) {
        if (sequence.isEmpty()) return oldStates;

        List<Integer> statesSet = new ArrayList<>();

        for (int state : oldStates) {
            // advance the state via the given sequence of symbols
            for (int symbol : sequence) {
                state = adjLists[state].get(symbol);
            }

            statesSet.add(state);
        }

        // using HashSet to remove duplicate states
        return new ArrayList<>(new HashSet<>(statesSet));
    }

    /**
     * BFS through a "forest" graph, where all the nodes are tuples (x, y).
     * We stop the BFS when we find a tuple that has the nodes equal (n, n).
     * The starting tuple is given by the nodes s and t (we assume these are
     * different nodes).
     *
     * All the graph nodes will be converted to forest node by this formula:
     * forestNode[k] = graphNode[i] * nodesCount + graphNode[j]
     *
     * @param finalStates - if we have a final states list given, then we also
     *                    check if the singleton node we reached(n,n) has the
     *                    node n in the final states. Searches other singleton
     *                    otherwise until we find one.
     *
     * After that, we compute the merging sequence by backtracking via the
     * parents until we reach the root.
     *
     * @return a list with each element from the merging sequence
     * */
    List<Integer> forestBFS(int s, int t, List<Integer> finalStates) {
        int startingNode = s * nodesCount + t;
        int singletonNode = -1;

        boolean visited[] = new boolean[nodesCount * nodesCount];
        // used to reconstruct the merging sequence
        int previous[] = new int[nodesCount * nodesCount];
        Arrays.fill(previous, -1);

        LinkedList<Integer> queue = new LinkedList<>();

        // mark the current node as visited and save it in the queue
        visited[startingNode] = true;
        queue.add(startingNode);

        boolean foundSingleton = false;

        while (!queue.isEmpty() && !foundSingleton) {
            // extract a node from the queue
            int currentNode = queue.poll();

            // extract the tuple data
            int currentNodeS = currentNode / nodesCount;
            int currentNodeT = currentNode % nodesCount;

            // go through all the neighbor nodes
            Iterator<Integer> iteratorS = adjLists[currentNodeS].listIterator();
            Iterator<Integer> iteratorT = adjLists[currentNodeT].listIterator();

            while (iteratorS.hasNext() && iteratorT.hasNext()) {
                int neighborS = iteratorS.next();
                int neighborT = iteratorT.next();

                // convert the 2 neighbor nodes into a forest node
                int neighbor = 	neighborS * nodesCount + neighborT;

                if (!visited[neighbor]) {
                    // check for singleton node
                    if (neighborS == neighborT) {
                        // if we have a final states list given, also check
                        // if the graph node is a final state.
                        if (!finalStates.isEmpty()) {
                            if (isFinalState(neighborS, finalStates)) {
                                singletonNode = neighbor;
                                previous[neighbor] = currentNode;

                                // used to break from outer while loop
                                foundSingleton = true;
                                break;
                            }
                        } else {
                            // otherwise, save the singleton data and exit the
                            // loop
                            singletonNode = neighbor;
                            previous[neighbor] = currentNode;

                            // used to break from outer while loop
                            foundSingleton = true;
                            break;
                        }
                    }

                    // mark the neighbor as visited and save it in the queue
                    visited[neighbor] = true;
                    previous[neighbor] = currentNode;
                    queue.add(neighbor);
                }
            }
        }

        List<Integer> result = new ArrayList<>();

        if (singletonNode != -1) {
            // we have a merging sequence, so now we have to construct it
            int parent = previous[singletonNode];

            // iterate through to the parents until we reach the root node
            while (parent != -1) {
                // extract the data from the forest node
                int singletonNodeS = singletonNode / nodesCount;
                int singletonNodeT = singletonNode % nodesCount;

                int parentS = parent / nodesCount;
                int parentT = parent % nodesCount;

                // find the symbol that transitioned the parent node to this
                // singleton node
                for (int i = 0; i < adjLists[parentT].size(); ++i) {
                    if (adjLists[parentS].get(i) == singletonNodeS &&
                        adjLists[parentT].get(i) == singletonNodeT) {
                        result.add(i);
                    }
                }

                // move to the upper level
                singletonNode = parent;
                parent = previous[parent];
            }
        }

        // we went from the child up to the root, so we have to reverse the
        // sequence
        Collections.reverse(result);

        return result;
    }
}
