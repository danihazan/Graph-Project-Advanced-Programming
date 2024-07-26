package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

public class IncAgent implements Agent {
    String name;
    String inputTopicName;
    private Double inputVal;
    String outputTopicName;

    public IncAgent(String[] subs, String[] pubs){
        this.inputTopicName = subs[0];
        this.outputTopicName = pubs[0];
        this.inputVal = Double.NaN;
        //subscribe to input topic
        TopicManagerSingleton.get().getTopic(inputTopicName).subscribe(this);
        //add publisher to outout topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);

    }


    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {

    }

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
}
