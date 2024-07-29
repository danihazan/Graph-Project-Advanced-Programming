package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

import java.util.function.BinaryOperator;

/**
 * Represents an agent that performs binary operations on inputs from two topics and publishes the result to an output topic.
 * This agent implements the {@link Agent} interface and is capable of handling messages that contain numeric data.
 *
 * Fields:
 * - name: The name of the agent.
 * - firstTopicName: The name of the first topic from which this agent receives its first operand.
 * - firstInputVal: The first numeric value received from the first topic.
 * - secondTopicName: The name of the second topic from which this agent receives its second operand.
 * - secondInputVal: The second numeric value received from the second topic.
 * - outputTopicName: The name of the topic to which this agent publishes the result of the operation.
 * - operation: The binary operation (e.g., addition, subtraction) to apply to the inputs.
 */
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

    /**
     * Callback method to process messages received from subscribed topics.
     * It extracts the double values from messages and stores them based on the topic.
     * If both inputs are received and valid, it applies a binary operation and publishes the result to an output topic.
     *
     * @param topic The topic from which the message is received.
     * @param msg The message received from the topic, expected to contain a numeric double value.
     */
    @Override
    public void callback(String topic, Message msg) {
        //store msg value
        if(topic.equals(firstTopicName)){
            firstInputVal = msg.asDouble;
        }
        if(topic.equals(secondTopicName)){
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

    public String getFirstTopicName() {
        return firstTopicName;
    }

    public String getOutputTopicName() {
        return outputTopicName;
    }
    public String getSecondTopicName(){
        return secondTopicName;
    }

    public BinaryOperator<Double> getOperation() {
        return operation;
    }
}
