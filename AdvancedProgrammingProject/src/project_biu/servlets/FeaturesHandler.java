package project_biu.servlets;

import project_biu.configs.*;
import project_biu.graph.Agent;
import project_biu.graph.TopicManagerSingleton;
import project_biu.server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * The FeaturesHandler class is a servlet responsible for handling features-related HTTP requests.
 * Handles "GET","/features" requests
 */
public class FeaturesHandler implements Servlet {
    /**
     * Handles the incoming request of type "GET","/features" and writes the response to the provided OutputStream.
     *
     * @param ri the RequestParser.RequestInfo object containing details about the client's request
     * @param toClient the OutputStream to which the response will be written
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        if(uriContainsString(ri.getUriSegments(),"eval")) {
            String httpResponse = showEvaluationsHandle();
            toClient.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            toClient.flush();
        }
    }


    /**
     * Closes the resource, performing any necessary cleanup.
     *
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void close() throws IOException {

    }
    /**
     * Generates an HTML representation to display graph's equations.
     *
     * @return a string containing the HTML content for displaying evaluations
     */
    private String showEvaluationsHandle(){
        GenericConfig gc=ConfLoader.gc;
        TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();
        List<Agent> agents=gc.getAgents();
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<html>\n");
        htmlTable.append("<body>\n");
        htmlTable.append("<h1> Topics Values </h1>\n");
        htmlTable.append("<table border='1'>");
        htmlTable.append("<tr><th>Agent</th><th>Equation</th><th>Result</th></tr>"); // Table header
        for(Agent agent:agents){
            if(agent instanceof ParallelAgent) {
                Agent a= ((ParallelAgent) agent).getAgent();

                switch (a) {
                    case IncAgent incAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>").append(incAgent.getOutputTopicName()).append("=").append(incAgent.getInputTopicName()).append("+1").append("</td>");
                        String lastMessage = tm.getTopic(incAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case PlusAgent plusAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>").append(plusAgent.getOutputTopicName()).append("=").append(plusAgent.getFirstTopicName()).append("+").append(plusAgent.getSecondTopicName()).append("</td>");
                        String lastMessage = tm.getTopic(plusAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case MulAgent mulAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>").append(mulAgent.getOutputTopicName()).append("=").append(mulAgent.getFirstTopicName()).append("*").append(mulAgent.getSecondTopicName()).append("</td>");
                        String lastMessage = tm.getTopic(mulAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case DivAgent divAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>").append(divAgent.getOutputTopicName()).append("=").append(divAgent.getFirstTopicName()).append("/").append(divAgent.getSecondTopicName()).append("</td>");
                        String lastMessage = tm.getTopic(divAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }
                    case ExponnentAgent exponnentAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>").append(exponnentAgent.getOutputTopicName()).append("=").append(exponnentAgent.getFirstTopicName()).append("^").append(exponnentAgent.getSecondTopicName()).append("</td>");
                        String lastMessage = tm.getTopic(exponnentAgent.getOutputTopicName()).getLastMessage();
                        String result = (lastMessage != null) ? lastMessage : "Not Determined";
                        htmlTable.append("<td>").append(result).append("</td>");
                        htmlTable.append("</tr>");
                    }

                    default -> {
                    }
                }
            }

        }
        htmlTable.append("    </table>\n");
        htmlTable.append("</body>\n");
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + htmlTable.toString().getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + htmlTable.toString();
        return httpResponse;
    }
    /**
     * Helper method checks if the target string is contained within the URI segments.
     *
     * @param uriSegment an array of URI segments
     * @param target the target string to search for within the URI segments
     * @return true if the target string is found within the URI segments, false otherwise
     */
    private boolean uriContainsString(String[] uriSegment,String target) {
        return Arrays.asList(uriSegment).contains(target);
    }

}
