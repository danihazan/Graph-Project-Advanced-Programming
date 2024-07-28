package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

public class PlusAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    static int counter = 1;

    public PlusAgent(String[] subs, String[] pubs){
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        this.name="PlusAgent "+counter;
        //subscribe to input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);
        //add publisher to outout topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);

    }
    @Override
    public String getName() {
        return name;
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
    public String getFirstTopicName(){return firstTopicName;}
    public String getSecondTopicName(){return secondTopicName;}
    public String getOutputTopicName(){return outputTopicName;}



}
