package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/**
 * Represents an agent that performs addition operations on inputs from two topics and publishes the result to an output topic.
 * This agent implements the {@link Agent} interface and handles the arithmetic addition of two numeric values.
 *
 * Fields:
 * - name: The name of the agent, dynamically generated with a counter to ensure uniqueness.
 * - firstTopicName: The name of the first topic from which this agent receives the first addend.
 * - firstInputVal: The first numeric value received from the first topic.
 * - secondTopicName: The name of the second topic from which this agent receives the second addend.
 * - secondInputVal: The second numeric value received from the second topic.
 * - outputTopicName: The name of the output topic to which this agent publishes the sum.
 * - counter: A static counter used to track the number of instances created and to help generate unique agent names.
 */
public class PlusAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    static int counter = 1;

    /**
     * Constructor for PlusAgent that initializes its subscriptions to input topics and its role as a publisher to an output topic.
     * Initializes the agent's name with a unique identifier and sets initial values for input values to NaN, indicating they are not yet received.
     *
     * @param subs An array of topic names to subscribe to, where subs[0] is the first input topic and subs[1] is the second input topic.
     * @param pubs An array of topic names where the agent will publish results; pubs[0] is the output topic.
     */
    public PlusAgent(String[] subs, String[] pubs){
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        this.name="PlusAgent "+counter;
        counter++;
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

    /**
     * Callback method that processes messages received from subscribed topics, performing addition on received values and publishing the result.
     * If both addend values are received and valid, it calculates the sum and publishes it to the output topic.
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
        if(topic.equals(secondTopicName)){
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
