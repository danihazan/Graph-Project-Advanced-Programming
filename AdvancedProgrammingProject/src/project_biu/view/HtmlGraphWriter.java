package project_biu.view;
import project_biu.configs.*;
import project_biu.graph.Agent;
import project_biu.graph.Graph;
import project_biu.servlets.ConfLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HtmlGraphWriter {

    // Method to generate HTML from the graph object
    public static String getGraphHTML(Graph graph) throws IOException {
        // Load the HTML template from the file located two directories back in "html_files"
        // Get the current working directory
        Path currentDir = Paths.get(System.getProperty("user.dir"));

        // Construct the path to the HTML template relative to the current directory
        Path templatePath = currentDir.resolve("AdvancedProgrammingProject/html_files/graph.html");
        String template = Files.readString(templatePath, StandardCharsets.UTF_8);

        // Generate JavaScript array for nodes with coordinates and messages if they exist
        StringBuilder nodesJsArray = new StringBuilder("[\n");
        Map<String, String> equationsMap=getNodesEquation();
        System.out.println(equationsMap);
        for (Node node : graph) {
            if (nodesJsArray.length() > 2) { // Check if not the first entry
                nodesJsArray.append(",\n");
            }
            String shape = getNodeType(node.getName()).equals("topic") ? "box" : "ellipse";
            String color = getNodeType(node.getName()).equals("topic") ? "lightyellow" : "lightblue"; // Light yellow for topics, light blue for agents
            String label = node.getName().substring(1);
            String equation = getNodeType(node.getName()).equals("agent") ? equationsMap.get(label) : "Topic"; // Light yellow for topics, light blue for agents
            String nodeLabel = getNodeType(node.getName()).equals("topic") ? "Topic: " + label : "Agent: " + label;
            String msg = node.getMsg() != null ? node.getMsg().asText : "null";
            nodesJsArray.append(String.format("{ id: '%s', label: '%s', shape: '%s', color: { background: '%s', border: 'black' }, message: '%s', equation: '%s' }",
                    node.getName(), nodeLabel, shape, color, msg, equation));
        }
        nodesJsArray.append("\n]");

        // Generate JavaScript array for edges
        StringBuilder edgesJsArray = new StringBuilder("[\n");
        for (Node node : graph) {
            for (Node edge : node.getEdges()) {
                if (edgesJsArray.length() > 2) { // Check if not the first entry
                    edgesJsArray.append(",\n");
                }
                edgesJsArray.append(String.format("{ from: '%s', to: '%s' }", node.getName(), edge.getName()));
            }
        }
        edgesJsArray.append("\n]");

        System.out.println(nodesJsArray.toString());
        // Replace placeholders in the template with generated JavaScript arrays
        String result = template.replace("/* NODES_PLACEHOLDER */", nodesJsArray.toString())
                .replace("/* EDGES_PLACEHOLDER */", edgesJsArray.toString());

        return result;
    }

    // Helper method to determine the node type based on its name
    private static String getNodeType(String name) {
        if (name.startsWith("T")) {
            return "topic";
        } else{
            return "agent";
        }
    }
    private static Map<String, String> getNodesEquation() {
        GenericConfig gc = ConfLoader.gc;
        List<Agent> agents = gc.getAgents();
        Map<String, String> nodeEquationMap = new HashMap<>();
        for (Agent agent : agents) {
            if (agent instanceof ParallelAgent) {
                Agent a = ((ParallelAgent) agent).getAgent();
                StringBuilder equation = new StringBuilder();
                equation.append("");
                switch (a) {
                    case IncAgent incAgent -> {

                        equation.append(incAgent.getOutputTopicName()).append("=").append(incAgent.getInputTopicName()).append("+1");

                    }
                    case PlusAgent plusAgent -> {
                        equation.append(plusAgent.getOutputTopicName()).append("=").append(plusAgent.getFirstTopicName()).append("+").append(plusAgent.getSecondTopicName());
                    }
                    case BinOpAgent binOpAgent -> {
                        equation.append(binOpAgent.getOutputTopicName()).append("=").append(binOpAgent.getFirstTopicName()).append(binOpAgent.getOperation()).append(binOpAgent.getSecondTopicName());
                    }
                    default -> {
                    }

                }
                nodeEquationMap.put(a.getName(),equation.toString());
            }
        }
        return nodeEquationMap;
    }

}
