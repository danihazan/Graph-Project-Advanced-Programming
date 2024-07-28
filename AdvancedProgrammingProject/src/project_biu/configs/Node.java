package project_biu.configs;

import project_biu.graph.Message;

import java.util.*;


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