package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {
    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    BinaryOperator<Double> operation;
    public BinOpAgent(String name, String firstTopicName, String secondTopicName, String outputTopicName, BinaryOperator<Double> operation){
        this.name=name;
        this.firstTopicName = firstTopicName;
        this.secondTopicName = secondTopicName;
        this.outputTopicName = outputTopicName;
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        this.operation = operation;
        //subscribe to input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);
        //add publisher to outout topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);

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
        if((!Double.isNaN(firstInputVal)) && (!Double.isNaN(secondInputVal))){
            Double outputVal= operation.apply(firstInputVal, secondInputVal);
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    @Override
    public void close() {

    }
    public String getName() {
        return name;
    }

    @Override
    public void reset() {

    }
}
