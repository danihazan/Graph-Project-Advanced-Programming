package view;
import graph.Graph;
import configs.Node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Collectors;

public class HtmlGraphWriter {

    // Method to generate HTML from the graph object
    public static String getGraphHTML(Graph graph) throws IOException {
        // Load the HTML template from the file located two directories back in "html_files"
        String templatePath = Paths.get(System.getProperty("user.dir")).toString()+"/AdvancedProgrammingProject/html_files/graph.html";
        String template = Files.readString(Paths.get(templatePath));

        // Generate JavaScript array for nodes with coordinates and messages if they exist
        StringBuilder nodesJsArray = new StringBuilder("[\n");
        for (Node node : graph) {
            if (nodesJsArray.length() > 2) { // Check if not the first entry
                nodesJsArray.append(",\n");
            }
            String shape = getNodeType(node.getName()).equals("topic") ? "box" : "ellipse";
            String color = getNodeType(node.getName()).equals("topic") ? "lightyellow" : "lightblue"; // Light yellow for topics, light blue for agents
            String label = node.getName().substring(1);
            String nodeLabel = getNodeType(node.getName()).equals("topic") ? "Topic: " + label : "Agent: " + label;
            String msg = node.getMsg() != null ? node.getMsg().asText : "null";
            nodesJsArray.append(String.format("{ id: '%s', label: '%s', shape: '%s', color: { background: '%s', border: 'black' }, message: '%s' }",
                    node.getName(), nodeLabel, shape, color, msg));
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
}
