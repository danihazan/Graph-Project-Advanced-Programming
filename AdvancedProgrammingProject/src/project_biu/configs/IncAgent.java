package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/**
 * Represents an agent that increments a received numeric value from an input topic and publishes the result to an output topic.
 * This agent implements the {@link Agent} interface and handles incremental operations to demonstrate simple data manipulation in a publish-subscribe model.
 *
 * Fields:
 * - name: The name of the agent, dynamically generated with a counter to ensure uniqueness.
 * - inputTopicName: The name of the input topic from which this agent receives numeric values to increment.
 * - inputVal: The numeric value received from the input topic.
 * - outputTopicName: The name of the output topic to which this agent publishes the incremented result.
 * - counter: A static counter used to track the number of instances created and to help generate unique agent names.
 */
public class IncAgent implements Agent {
    String name;
    String inputTopicName;
    private Double inputVal;
    String outputTopicName;
    static int counter = 1;

    /**
     * Constructor for IncAgent that initializes its subscription to an input topic and its role as a publisher to an output topic.
     * Initializes the agent's name with a unique identifier and sets the initial input value to NaN, indicating it has not yet been received.
     *
     * @param subs An array of topic names to subscribe to; expects a single entry representing the input topic.
     * @param pubs An array of topic names where the agent will publish results; expects a single entry representing the output topic.
     */
    public IncAgent(String[] subs, String[] pubs){
        this.inputTopicName = subs[0];
        this.outputTopicName = pubs[0];
        this.inputVal = Double.NaN;
        this.name="IncAgent "+counter;
        counter++;
        //subscribe to input topic
        TopicManagerSingleton.get().getTopic(inputTopicName).subscribe(this);
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
     * Callback method that processes messages received from the subscribed input topic. It increments the received numeric value by 1 and publishes the result to the output topic.
     * This method ensures that the operation is only performed if a valid numeric value is received.
     *
     * @param topic The topic from which the message is received. Expected to be the input topic for this agent.
     * @param msg The message containing the numeric value to be incremented.
     */
    @Override
    public void callback(String topic, Message msg) {
        inputVal = msg.asDouble;
        if(!Double.isNaN(inputVal))
        {
            Double outputVal= inputVal+1;
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));

        }
    }

    @Override
    public void close() {

    }
    public String getInputTopicName(){return inputTopicName;}
    public String getOutputTopicName(){return outputTopicName;}

}
