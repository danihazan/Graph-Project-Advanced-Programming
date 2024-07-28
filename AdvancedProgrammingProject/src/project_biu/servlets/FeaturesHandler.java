package project_biu.servlets;

import project_biu.configs.*;
import project_biu.graph.Agent;
import project_biu.graph.Graph;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;
import project_biu.server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FeaturesHandler implements Servlet {

    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        if(uriContainsString(ri.getUriSegments(),"eval")) {
            String httpResponse = showEvaluationsHandle();
            toClient.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            toClient.flush();
            System.out.println(httpResponse);
        }
        else if(uriContainsString(ri.getUriSegments(),"reset")){
            resetHandle();
        }
    }

    @Override
    public void close() throws IOException {

    }

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
                    case BinOpAgent binOpAgent -> {
                        htmlTable.append("<tr>");
                        htmlTable.append("<td>").append(a.getName()).append("</td>");
                        htmlTable.append("<td>").append(binOpAgent.getOutputTopicName()).append("=").append(binOpAgent.getFirstTopicName()).append(binOpAgent.getOperation()).append(binOpAgent.getSecondTopicName()).append("</td>");
                        String lastMessage = tm.getTopic(binOpAgent.getOutputTopicName()).getLastMessage();
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

    private void resetHandle(){
        System.out.println("reset");
        ConfLoader.gc.close();
        ConfLoader.gc=null;
        TopicManagerSingleton.get().clear();

    }
    private boolean uriContainsString(String[] uriSegmant,String target) {
        return Arrays.asList(uriSegmant).contains(target);
    }


}
