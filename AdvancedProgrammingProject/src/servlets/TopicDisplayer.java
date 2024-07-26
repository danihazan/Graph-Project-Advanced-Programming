package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;

public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        System.out.println(requestInfo.getParameters());
        System.out.println("Topic display");

        //extract topic name and message from request
        String topic=requestInfo.getParameters().get("topic");
        String message=requestInfo.getParameters().get("message");
        System.out.println(topic);
        System.out.println(message);

        //post message to topic
        TopicManagerSingleton.get().getTopic(topic).publish(new Message(message));



        System.out.println("updated topic "+topic);
        //create http response
        //create topics+messages table
        StringBuilder htmlResponse=new StringBuilder();
        htmlResponse.append("<html>\n");
        htmlResponse.append("<body>\n");
        htmlResponse.append("<h1> Topics Values </h1>\n");
        htmlResponse.append("   <table border=\"1\">\n");
        htmlResponse.append("       <tr>\n");
        htmlResponse.append("           <th>Topic</th>\n");
        htmlResponse.append("           <th>Last Value</th>\n");
        htmlResponse.append("       </tr>\n");
        for(Topic t:TopicManagerSingleton.get().getTopics()) {
            htmlResponse.append("        <tr>\n");
            htmlResponse.append("            <td>").append(t.name).append("</td>\n");
            htmlResponse.append("            <td>").append(t.getLastMessage()).append("</td>\n");
            htmlResponse.append("        </tr>\n");
        }
        htmlResponse.append("    </table>\n");
        htmlResponse.append("</body>\n");
        String httpResponse="HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + htmlResponse.toString().getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + htmlResponse.toString();
        System.out.println(httpResponse);
        toClient.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
    }
}
