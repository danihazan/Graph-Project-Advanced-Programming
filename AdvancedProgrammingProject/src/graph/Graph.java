package graph;

import configs.Node;

import java.util.ArrayList;
import java.util.Collection;

public class Graph extends ArrayList<Node>{
    public Graph() {
        super();
    }

    public boolean hasCycles() {
        for (Node node : this) {
            if(node.hasCycles()) {
            return true;
            }
        }
        return false;
    }
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
    //function returns node from list if exists else creates node with the same name and insert it to arraylist
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
