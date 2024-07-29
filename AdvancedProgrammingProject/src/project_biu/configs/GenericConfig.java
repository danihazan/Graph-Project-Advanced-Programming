package project_biu.configs;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import project_biu.graph.Agent;

/**
 * Implements the Config interface to provide configuration settings through a file-based approach.
 * This class manages the configuration of agents, allowing settings to be loaded and modified via a configuration file.
 *
 * Fields:
 * - configFileName: The name of the configuration file used to load and save settings.
 * - agents: A list of agents that are configured through this configuration file.
 */
public class GenericConfig implements Config {

    String configFileName;

    private List<Agent> agents = new ArrayList<>();

    public void setConfFile(String confFile) {
        this.configFileName = confFile;
    }

    /**
     * Creates and initializes configuration settings from a specified file. It reads agent configurations and dynamically instantiates agents based on class names provided in the file.
     * This method processes each line as a separate agent configuration, which includes the class name, subscription topics, and publication topics.
     * Exceptions are thrown for file reading errors, class loading issues, or constructor problems, ensuring robust error handling.
     *
     * @throws Exception If there is an error reading the configuration file, or if an error occurs during agent instantiation.
     */
    @Override
    public void create() throws Exception {
        List<String> lines = List.of();
        Path filePath = Paths.get(this.configFileName);
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new Exception("Error reading configuration file: " + this.configFileName + ". ");
        }

        if(ifConfigFileValid(lines)) {
            for (int i = 0; i < lines.size(); i += 3) {

                try {
                    String className = lines.get(i);
                    String[] subs = lines.get(i + 1).split(",");
                    String[] pubs = lines.get(i + 2).split(",");

                    Class<?> agentClass = Class.forName(className);
                    Constructor<?> constructor = agentClass.getConstructor(String[].class, String[].class);
                    Object agent = constructor.newInstance((Object) subs, (Object) pubs);
                    ParallelAgent p_agent= new ParallelAgent((Agent) agent, 1);
                    this.agents.add(p_agent);


                } catch (ClassNotFoundException e) {
                    throw new Exception("Class not found: " + lines.get(i) + ". ");
                } catch (NoSuchMethodException e) {
                    throw new Exception("Constructor not found for class: " + lines.get(i) + ". ");
                } catch (Exception e) {
                    throw new Exception("Error creating agent instance for class: " + lines.get(i) + ". ");
                }
            }
        } else {
            throw new Exception("Invalid configuration file please follow Config file instructions.\n Number of lines must divide by 3. \n Block should contain 1 or 2 Topics according to class.");
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void close() {
        for (Agent a : agents) {
            a.close();
        }
    }

    /**
     * Validates the configuration file format. Checks if the number of lines in the configuration file is correct and divisible by three,
     * indicating a consistent block structure where each block defines an agent along with its subscriptions and publications.
     * Each block is further validated for correct formatting and content.
     *
     * @param lines A list of strings representing the lines read from the configuration file.
     * @return true if the configuration file is valid and follows the required format, false otherwise.
     */
    private boolean ifConfigFileValid(List<String> lines) {
        //check if number of lines is divided by 3
        int numberOfLines = lines.size();
        if (!(numberOfLines > 0 && numberOfLines % 3 == 0))
        {
            return false;
        }

        //check if blocks are valid
        for (int i = 0; i < lines.size(); i += 3) {
            if (!isValidAgentBlock(lines, i)) {
                return false;
            }
        }
        return true;

    }

    /**
     * Validates a specific block of configuration data for an agent. This method checks that no line in the block is empty and attempts to load the class specified in the block.
     * A block is considered valid if the class can be found and loaded, and all lines are non-empty.
     *
     * @param lines The entire list of configuration lines.
     * @param index The starting index of the block to validate within the lines list.
     * @return true if the block is valid, indicating that the class exists and there are no empty lines in the block; false otherwise.
     */
    private boolean isValidAgentBlock(List<String> lines, int index) {
        //check for empty lines in block
        if (lines.get(index).trim().isEmpty() || lines.get(index + 1).trim().isEmpty() || lines.get(index + 2).trim().isEmpty()) return false;

        // Validate class name
        String className = lines.get(index).trim();
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
            return false;
        }
        return true;
    }
    public int getNumberOfAgents(){
        return agents.size();
    }

    public List<Agent> getAgents(){
        return this.agents;

    }

}
