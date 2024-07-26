package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

public class PlusAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;

    public PlusAgent(String[] subs, String[] pubs){
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        //subscribe to input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);
        //add publisher to outout topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);

    }
    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {

    }

    @Override
    public void callback(String topic, Message msg) {
        //store msg value
        if(topic.equals(firstTopicName)){
            firstInputVal = msg.asDouble;
        }
        else if(topic.equals(secondTopicName)){
            secondInputVal = msg.asDouble;

        }
        //check if both topics sent Double if so calculate output
        if (!Double.isNaN(firstInputVal) && !Double.isNaN(secondInputVal)) {
            Double outputVal= firstInputVal+secondInputVal;
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }

    }

    @Override
    public void close() {

    }



}
