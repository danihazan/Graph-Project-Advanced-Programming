package project_biu.graph;

import project_biu.configs.Node;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Extends ArrayList to represent a graph structure composed of nodes. This graph is specifically designed to detect cycles within its structure.
 * Utilizes a list of nodes where each node can be connected to other nodes forming a directed graph.
 *
 * Methods:
 * - hasCycles(): Checks if there are any cycles in the graph, indicating circular dependencies or loops.
 */
public class Graph extends ArrayList<Node>{
    public Graph() {
        super();
    }

    /**
     * Determines if the graph contains any cycles by checking each node for cyclic connections.
     * @return true if at least one cycle is found in the graph, false otherwise.
     */
    public boolean hasCycles() {
        for (Node node : this) {
            if(node.hasCycles()) {
            return true;
            }
        }
        return false;
    }

    /**
     * Constructs the graph by retrieving topics from the TopicManager and creating nodes for each topic and its subscribed and publishing agents.
     * Nodes for agents are connected to topic nodes to represent the flow of messages between topics and agents.
     *
     * Uses the TopicManagerSingleton to access topics and their associated subscriptions and publications to build the graph.
     */
    public void createFromTopics(){
        Node topicNode;
        Node agentSubNode;
        Node agentPubNode;
        TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();
        Collection<Topic> topics= tm.getTopics();
        for (Topic topic : topics) {
            topicNode= getNodeByName("T"+topic.name);
            for (Agent sub :topic.subs){
                agentSubNode=getNodeByName("A"+sub.getName());
                topicNode.addEdge(agentSubNode);
            }
            for (Agent pub :topic.pubs){
                agentPubNode=getNodeByName("A"+pub.getName());
                agentPubNode.addEdge(topicNode);
            }
        }
    }

    /**
     * Retrieves a node by its name from the graph. If the node does not exist, it creates a new node with the specified name and adds it to the graph.
     * This method ensures that each node in the graph has a unique name and facilitates the linking of nodes based on their names.
     *
     * @param name The name of the node to retrieve or create.
     * @return The node associated with the given name.
     */
    private Node getNodeByName(String name) {
        for (Node node : this) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        Node newNode = new Node(name);
        this.add(newNode);
        return newNode;
    }

}
