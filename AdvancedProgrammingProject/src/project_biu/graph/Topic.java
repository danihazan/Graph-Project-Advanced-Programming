package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The Topic class represents a topic to which agents can subscribe and publish messages.
 */
public class Topic {
    public final String name;
    List<Agent> pubs;
    List<Agent> subs;
    private String lastMessage;

    /**
     * Constructs a new Topic with the specified name.
     *
     * @param name the name of the topic
     */
    Topic(String name) {
        this.name = name;
        this.pubs = new ArrayList<>();
        this.subs = new ArrayList<>();
    }
    /**
     * Subscribes an agent to this topic.
     *
     * @param a the agent to subscribe
     */
    public void subscribe(Agent a) {
        this.subs.add(a);
    }

    /**
     * Unsubscribes an agent from this topic.
     *
     * @param a the agent to unsubscribe
     */
    public void unsubscribe(Agent a) {
        this.subs.remove(a);
    }

    /**
     * Publishes a message to all subscribed agents.
     *
     * @param m the message to publish
     */
    public void publish(Message m) {
        lastMessage=m.asText;
        for(Agent a : this.subs) {
            a.callback(name,m);
            System.out.println("publishing message "+m.asText+" to "+a.getName());
        }
    }
    /**
     * Adds an agent as a publisher to this topic.
     *
     * @param a the agent to add as a publisher
     */
    public void addPublisher(Agent a) {
        this.pubs.add(a);
    }

    /**
     * Removes an agent as a publisher from this topic.
     *
     * @param a the agent to remove as a publisher
     */
    public void removePublisher(Agent a) {
        this.pubs.remove(a);
    }

    /**
     * Returns the last message published to this topic.
     *
     * @return the last message published to this topic
     */
    public String getLastMessage() {
        return lastMessage;
    }
}