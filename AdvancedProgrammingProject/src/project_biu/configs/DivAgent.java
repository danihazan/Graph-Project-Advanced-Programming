package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/**
 * Represents an agent that performs division operations on inputs from two topics and publishes the result to an output topic.
 * This agent implements the {@link Agent} interface and specifically handles division to avoid division by zero errors.
 *
 * Fields:
 * - name: The name of the agent.
 * - firstTopicName: The name of the first topic from which this agent receives its numerator value.
 * - firstInputVal: The numerator value received from the first topic.
 * - secondTopicName: The name of the second topic from which this agent receives its denominator value.
 * - secondInputVal: The denominator value received from the second topic.
 * - outputTopicName: The name of the topic to which this agent publishes the result of the division.
 * - counter: A static counter used to keep track of instance creation or other operational metrics.
 */
public class DivAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    static int counter = 1;

    /**
     * Constructor for DivAgent that initializes its subscriptions to input topics and its role as a publisher to an output topic.
     * Initializes the agent's name and increments the static counter upon creation.
     *
     * @param subs An array of topic names to subscribe to, where subs[0] is the first input topic and subs[1] is the second input topic.
     * @param pubs An array of topic names where the agent will publish results; pubs[0] is the output topic.
     */
    public DivAgent(String[] subs, String[] pubs){
        //update topics
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        //update Agent Name
        this.name="DivAgent "+counter;
        counter++;
        //subscribe to input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);
        //add publisher to output topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);

    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {}

    /**
     * Processes messages from subscribed topics, performing division on received values and publishing the result.
     * Ensures that division is only performed when both numerator and denominator values are available and valid.
     * Handles division by zero by not performing the division if the denominator is zero.
     *
     * @param topic The topic from which the message is received.
     * @param msg The message containing the numeric value related to the topic.
     */
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
            double outputVal= firstInputVal/secondInputVal;
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
