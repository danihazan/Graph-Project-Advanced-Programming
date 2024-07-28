package project_biu.graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

/**
 * The TopicManagerSingleton class provides a singleton instance of the TopicManager,
 * which manages topics within a concurrent hash map.
 */

public class TopicManagerSingleton {

    /**
     * Returns the singleton instance of the TopicManager.
     *
     * @return the singleton instance of the TopicManager
     */
    public static TopicManager get() {
        return TopicManager.instance;
    }

    /**
     * The TopicManager class is responsible for managing topics in a thread-safe manner.
     */
    public static class TopicManager{
        private static final TopicManager instance=new TopicManager();
        ConcurrentHashMap<String, Topic> topicMap;


        /**
         * Private constructor to prevent instantiation from outside the class.
         */
        private TopicManager() {
            topicMap=new ConcurrentHashMap<>();
        }

        /**
         * Retrieves a topic by its name. If the topic does not exist, it creates a new one.
         *
         * @param topicName the name of the topic
         * @return the Topic object
         */
        public Topic getTopic(String topicName) {
            Topic topic=topicMap.get(topicName);
            if(topic==null) {
                topic=new Topic(topicName);
                topicMap.put(topicName, topic);
            }
            return topic;
        }

        /**
         * Retrieves a collection of all topics.
         *
         * @return a collection of all topics
         */
        public Collection<Topic> getTopics() {
            return topicMap.values();
        }

        /**
         * Clears all topics from the topic map.
         */
        public void clear(){
            topicMap.clear();
        }


    }

    
}
