package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    List<Agent> pubs;
    List<Agent> subs;
    private String lastMessage;

    Topic(String name) {
        this.name = name;
        this.pubs = new ArrayList<>();
        this.subs = new ArrayList<>();
    }

    public void subscribe(Agent a) {
        this.subs.add(a);
    }

    public void unsubscribe(Agent a) {
        this.subs.remove(a);
    }

    public void publish(Message m) {
        lastMessage=m.asText;
        for(Agent a : this.subs) {
            a.callback(name,m);
            System.out.println("publishing message "+m.asText+" to "+a.getName());
        }
    }

    public void addPublisher(Agent a) {
        this.pubs.add(a);
    }

    public void removePublisher(Agent a) {
        this.pubs.remove(a);
    }

    public String getLastMessage() {
        return lastMessage;
    }
}