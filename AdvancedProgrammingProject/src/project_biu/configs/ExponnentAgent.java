package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/**
 * Represents an agent that performs exponential operations on inputs from two topics and publishes the result to an output topic.
 * This agent implements the {@link Agent} interface and handles calculations of the form firstInputVal^secondInputVal.
 *
 * Fields:
 * - name: The name of the agent, dynamically generated with a counter to ensure uniqueness.
 * - firstTopicName: The name of the first topic from which this agent receives the base value for the exponentiation.
 * - firstInputVal: The base value received from the first topic.
 * - secondTopicName: The name of the second topic from which this agent receives the exponent value.
 * - secondInputVal: The exponent value received from the second topic.
 * - outputTopicName: The name of the topic to which this agent publishes the result of the exponentiation.
 * - counter: A static counter used to track the number of instances created and to help generate unique agent names.
 */
public class ExponnentAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    static int counter = 1;

    /**
     * Constructor for ExponentAgent that initializes its subscriptions to input topics and its role as a publisher to an output topic.
     * Initializes the agent's name with a unique identifier and sets initial values for input values to NaN, indicating they are not yet received.
     *
     * @param subs An array of topic names to subscribe to, where subs[0] is the base value topic and subs[1] is the exponent value topic.
     * @param pubs An array of topic names where the agent will publish results; pubs[0] is the output topic.
     */
    public ExponnentAgent(String[] subs, String[] pubs){
        //update topics
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        //update Agent Name
        this.name="ExponnentAgent "+counter;
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
     * Callback method that handles messages received on subscribed topics. It performs exponential calculations using the received values.
     * If both the base and exponent values are received and valid, it calculates the power of the base raised to the exponent and publishes the result.
     *
     * @param topic The topic from which the message is received.
     * @param msg The message containing the numeric value related to the topic. Expected to be parseable as a double.
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
            double outputVal = Math.pow(firstInputVal, secondInputVal);
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
