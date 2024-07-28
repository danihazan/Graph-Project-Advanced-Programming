package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Represents a wrapper for an {@link Agent} that processes messages in parallel using a separate thread.
 * This class enhances message handling efficiency by queuing messages and processing them asynchronously.
 *
 * Fields:
 * - agent: The underlying agent that actually processes the messages.
 * - queue: A blocking queue used to hold messages until they can be processed by the agent.
 * - stop: A flag to control the stopping of the message processing thread when no longer needed.
 */
public class ParallelAgent implements Agent {

    Agent agent;
    ArrayBlockingQueue<Message> queue;
    private boolean stop = false;

    /**
     * Constructs a ParallelAgent with a specified message processing capacity.
     * Initializes a separate thread to handle message processing asynchronously from a queue.
     * This constructor sets up the agent to process messages and manage flow control via a blocking queue.
     *
     * @param agent The agent responsible for actual message processing.
     * @param capacity The maximum number of messages that can be queued before blocking further submissions.
     */
    public ParallelAgent(Agent agent , int capacity){
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<Message>(capacity);
        new Thread(new Runnable() {
            public void run() {
                while(!stop){
                    try{
                        Message message = queue.take();// if queue is empty thread goes to sleep (even though loop is true)
                        //retrieve original message and topic
                        // Define the regex pattern
                        Pattern pattern = Pattern.compile("Topic:(.*?) Message:(.*)");
                        Matcher matcher = pattern.matcher(message.asText);
                        String topic;
                        String originalMessage;
                        if (matcher.matches()) {
                            // Extract topic and message using groups
                            topic = matcher.group(1).trim();
                            originalMessage = matcher.group(2).trim();
                            agent.callback(topic,new Message(originalMessage));
                        }
                        else{
                            topic = message.asText;
                            if(topic.matches("stop")){
                                stop=true;
                            }
                        }



                    }
                    catch(InterruptedException e){

                    }
                }
            }
        }).start();
    }


    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }

    /**
     * Callback method that processes incoming messages by wrapping them with topic information and enqueuing them for parallel processing.
     * This method formats the message to include both the topic and the original message content, then adds it to the processing queue.
     *
     * @param topic The topic associated with the message.
     * @param msg The original message received.
     */
    @Override
    public void callback(String topic, Message msg) {
        String newmsgString = "Topic:" + topic + " Message:" + msg.asText;
        Message newMessage = new Message(newmsgString);
        try {
            queue.put(newMessage);
        }
        catch (InterruptedException e) {
        }
    }

    /**
     * Closes the ParallelAgent and its underlying agent. Ensures that all processing is completed and the message processing thread is stopped.
     * Adds a special "stop" message to the queue to signal the processing thread to terminate, and waits for the stop flag to be set before closing the underlying agent.
     */
    @Override
    public void close() {
        try {
            queue.put(new Message("stop"));
        }
        catch (InterruptedException e) {
        }
        while(!stop){}

        agent.close();
        agent.close();

    }
    public Agent getAgent(){return agent;}
}
