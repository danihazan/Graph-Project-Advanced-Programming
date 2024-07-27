package configs;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import graph.Agent;

public class GenericConfig implements Config {

    String configFileName;

    private List<Agent> agents = new ArrayList<>();

    public void setConfFile(String confFile) {
        this.configFileName = confFile;
    }

    // Method creates agents based on the configuration file
    @Override
    public void create() {
        List<String> lines = List.of();
        Path filePath = Paths.get(this.configFileName);
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            System.err.println("Error - " + e);
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


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
    for(Agent a :agents){
        a.close();
    }

    }

    //Config file handling
//    private List<String> readConfigFile() {
//        try {
//            return Files.readAllLines(Paths.get(configFileName));
//        } catch (IOException e) {
//            System.err.println("Error reading configuration file: " + e.getMessage());
//            return null;
//        }
//    }
    private boolean ifConfigFileValid(List<String> lines) {
        //check if number of lines is divided by 3
        int numberOfLines = lines.size();
        if (!(numberOfLines > 0 && numberOfLines % 3 == 0))
        {
            System.out.println("1 " + numberOfLines);
            return false;
        }

        //check if blocks are valid
        for (int i = 0; i < lines.size(); i += 3) {
            if (!isValidAgentBlock(lines, i)) {
                System.err.println("Invalid configuration block starting at line " + (i + 1));
                return false;
            }
        }
        return true;

    }    //Validation for Config File
    private boolean isValidAgentBlock(List<String> lines, int index) {
        //check for empty lines in blocc
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

    public boolean VerifyIncAgentExist() {
        for(Agent a: this.agents) {
            if (Objects.equals(a.getName(), "IncAgent")) {
                return true;
            }
        }
        return false;
    }

    public boolean DoesPlusAgentExist() {
        for(Agent a: this.agents) {
            if (Objects.equals(a.getName(), "PlusAgent")) {
                return true;
            }
        }
        return false;
    }

    public List<Agent> getAgents(){
        return this.agents;

    }

}
