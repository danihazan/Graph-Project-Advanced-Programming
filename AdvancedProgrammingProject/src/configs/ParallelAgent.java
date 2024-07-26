package configs;

import graph.Agent;
import graph.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ArrayBlockingQueue;

public class ParallelAgent implements Agent {

    Agent agent;
    ArrayBlockingQueue<Message> queue;
    private boolean stop = false;

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
}
