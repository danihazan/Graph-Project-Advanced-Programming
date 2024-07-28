package project_biu.graph;

/**
 * Interface defining the core functionalities of an agent within a messaging or event-driven system.
 * Agents are expected to be able to receive and process messages, perform cleanup, and identify themselves by name.
 *
 * Methods:
 * - getName(): Retrieves the name of the agent.
 * - reset(): Resets the state of the agent to a clean initial state.
 * - callback(String topic, Message msg): Processes a received message that is published to a subscribed topic.
 * - close(): Performs any necessary finalization before the agent is shut down, such as releasing resources.
 */
public interface Agent {
    String getName();
    void reset();
    void callback(String topic, Message msg);
    void close();
}
