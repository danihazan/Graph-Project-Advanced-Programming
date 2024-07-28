package project_biu.configs;

import project_biu.graph.Message;

import java.util.*;

/**
 * Represents a node in a graph structure, typically used to model relationships or connections within a network.
 * Each node can have multiple edges to other nodes, and may also store a message relevant to its context.
 *
 * Fields:
 * - name: The unique identifier or label of the node.
 * - edges: A list of other nodes to which this node is connected.
 * - msg: A message associated with this node, potentially storing additional data or state.
 *
 * @param 'name' The name or identifier for the node, used to uniquely identify it within the graph.
 */
public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }
    public void addEdge(Node n) {
        edges.add(n);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Node> getEdges() {
        return edges;
    }
    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public boolean hasCycles() {
        Set<Node> visitedNodes = new HashSet<>();
        Set<Node> currentStack = new HashSet<>();

        return detectCycle(this, visitedNodes, currentStack);
    }

    /**
     * Detects cycles in a graph starting from the current node using depth-first search.
     * It checks if any node is revisited while it is still in the recursion stack, indicating a cycle.
     *
     * @param node The current node being visited in the graph.
     * @param visitedNodes A set of nodes that have already been visited in the search process to avoid reprocessing.
     * @param currentStack A set of nodes currently in the recursion stack; used to detect back edges indicating a cycle.
     * @return true if a cycle is detected involving the current node, false otherwise.
     */

    private boolean detectCycle(Node node, Set<Node> visitedNodes, Set<Node> currentStack) {
        if (currentStack.contains(node)) {
            return true; // Cycle detected
        }

        if (visitedNodes.contains(node)) {
            return false; // Node already processed
        }

        visitedNodes.add(node);
        currentStack.add(node);

        for (Node neighbor : node.edges) {
            if (detectCycle(neighbor, visitedNodes, currentStack)) {
                return true;
            }
        }

        currentStack.remove(node);
        return false;
    }
    public void printAllNeighbours(){
        for (Node neighbor : this.edges) {
            System.out.println(this.name+"->"+neighbor.name);
        }

    }

}