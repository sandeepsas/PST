package MaximumMatching;

/*****************************************************************************
 * File: MUndirectedGraph.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing an undirected graph where each edge has an associated 
 * real-valued length.  Internally, the class is represented by an adjacency
 * list where each edges appears twice - once in the forward direction and
 * once in the reverse.  In fact, this implementation was formed by taking
 * a standard adjacency list and then duplicating the logic to ensure each
 * edge appears twice.
 */
import java.util.*; // For HashMap, HashSet

public final class MUndirectedGraph<T> implements Iterable<T> {
    /* A map from nodes in the graph to sets of outgoing edges.  Each
     * set of edges is represented by a map from edges to doubles.
     */
    private final Map<T, Set<T>> mGraph = new HashMap<T, Set<T>>();

    /**
     * Adds a new node to the graph.  If the node already exists, this
     * function is a no-op.
     *
     * @param node The node to add.
     * @return Whether or not the node was added.
     */
    public boolean addNode(T node) {
        /* If the node already exists, don't do anything. */
        if (mGraph.containsKey(node))
            return false;

        /* Otherwise, add the node with an empty set of outgoing edges. */
        mGraph.put(node, new HashSet<T>());
        return true;
    }

    /**
     * Given a node, returns whether that node exists in the graph.
     *
     * @param The node in question.
     * @return Whether that node eixsts in the graph.
     */
    public boolean nodeExists(T node) {
        return mGraph.containsKey(node);
    }

    /**
     * Given two nodes, adds an arc of that length between those nodes.  If 
     * either endpoint does not exist in the graph, throws a 
     * NoSuchElementException.
     *
     * @param one The first node.
     * @param two The second node.
     * @throws NoSuchElementException If either the start or destination nodes
     *                                do not exist.
     */
    public void addEdge(T one, T two) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(one) || !mGraph.containsKey(two))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        /* Add the edge in both directions. */
        mGraph.get(one).add(two);
        mGraph.get(two).add(one);
    }

    /**
     * Removes the edge between the indicated endpoints from the graph.  If the
     * edge does not exist, this operation is a no-op.  If either endpoint does
     * not exist, this throws a NoSuchElementException.
     *
     * @param one The start node.
     * @param two The destination node.
     * @throws NoSuchElementException If either node is not in the graph.
     */
    public void removeEdge(T one, T two) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(one) || !mGraph.containsKey(two))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        /* Remove the edges from both adjacency lists. */
        mGraph.get(one).remove(two);
        mGraph.get(two).remove(one);
    }

    /**
     * Given two endpoints, returns whether an edge exists between them.  If
     * either endpoint does not exist in the graph, throws a 
     * NoSuchElementException.
     *
     * @param one The first endpoint.
     * @param two The second endpoint.
     * @return Whether an edge exists between the endpoints.
     * @throws NoSuchElementException If the endpoints are not nodes in the 
     *                                graph.
     */
    public boolean edgeExists(T one, T two) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(one) || !mGraph.containsKey(two))
            throw new NoSuchElementException("Both nodes must be in the graph.");     
        
        /* Graph is symmetric, so we can just check either endpoint. */
        return mGraph.get(one).contains(two);
    }

    /**
     * Given a node in the graph, returns an immutable view of the edges
     * leaving that node.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NoSuchElementException If the node does not exist.
     */
    public Set<T> edgesFrom(T node) {
        /* Check that the node exists. */
        Set<T> arcs = mGraph.get(node);
        if (arcs == null)
            throw new NoSuchElementException("Source node does not exist.");

        return Collections.unmodifiableSet(arcs);
    }

    /**
     * Returns whether a given node is contained in the graph.
     *
     * @param The node to test for inclusion.
     * @return Whether that node is contained in the graph.
     */
    public boolean containsNode(T node) {
        return mGraph.containsKey(node);
    }

    /**
     * Returns an iterator that can traverse the nodes in the graph.
     *
     * @return An iterator that traverses the nodes in the graph.
     */
    public Iterator<T> iterator() {
        return mGraph.keySet().iterator();
    }

    /**
     * Returns the number of nodes in the graph.
     *
     * @return The number of nodes in the graph.
     */
    public int size() {
        return mGraph.size();
    }

    /**
     * Returns whether the graph is empty.
     *
     * @return Whether the graph is empty.
     */
    public boolean isEmpty() {
        return mGraph.isEmpty();
    }

    /**
     * Returns a human-readable representation of the graph.
     *
     * @return A human-readable representation of the graph.
     */
    public String toString() {
        return mGraph.toString();
    }
}
