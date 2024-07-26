package graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

public class TopicManagerSingleton {


    public static TopicManager get() {
        return TopicManager.instance;
    }


    public static class TopicManager{
        private static final TopicManager instance=new TopicManager();
        ConcurrentHashMap<String, Topic> topicMap;

        //private constructor
        private TopicManager() {
            topicMap=new ConcurrentHashMap<>();
        }
        public Topic getTopic(String topicName) {
            Topic topic=topicMap.get(topicName);
            if(topic==null) {
                topic=new Topic(topicName);
                topicMap.put(topicName, topic);
            }
            return topic;
        }
        public Collection<Topic> getTopics() {
            return topicMap.values();
        }
        public void clear(){
            topicMap.clear();
        }

    }

    
}
