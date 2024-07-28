package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

import project_biu.graph.Message;
import project_biu.graph.Topic;
import project_biu.graph.TopicManagerSingleton;
import project_biu.server.RequestParser.RequestInfo;
/**
 * The TopicDisplayer class implements the Servlet interface and is responsible for displaying topics' table.
 *  * Handle "GET", "/publish" requests
 *
*/
public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {

            // Extract topic and Message from request
            String topic = requestInfo.getParameters().get("topic");
            String message = requestInfo.getParameters().get("message");
            TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();

            if(ConfLoader.gc!=null) {
                //post message to topic
                if(topicExists(topic)) {
                    tm.getTopic(topic).publish(new Message(message));


                    //create http response
                    //create topics+messages table
                    StringBuilder htmlResponse = new StringBuilder();
                    htmlResponse.append("<html>\n");
                    htmlResponse.append("<body>\n");
                    htmlResponse.append("<h1> Topics Values </h1>\n");
                    htmlResponse.append("   <table border=\"1\">\n");
                    htmlResponse.append("       <tr>\n");
                    htmlResponse.append("           <th>Topic</th>\n");
                    htmlResponse.append("           <th>Last Value</th>\n");
                    htmlResponse.append("       </tr>\n");
                    String topicName, topicMessage;
                    for (Topic t : TopicManagerSingleton.get().getTopics()) {
                        topicName = t.name;
                        topicMessage = t.getLastMessage();
                        if (topicMessage == null) topicMessage = "";
                        htmlResponse.append("        <tr>\n");
                        htmlResponse.append("            <td>").append(topicName).append("</td>\n");
                        htmlResponse.append("            <td>").append(topicMessage).append("</td>\n");
                        htmlResponse.append("        </tr>\n");
                    }
                    htmlResponse.append("    </table>\n");
                    htmlResponse.append("</body>\n");
                    String httpResponse = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: " + htmlResponse.toString().getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n" + htmlResponse.toString();
                    toClient.write(httpResponse.getBytes(StandardCharsets.UTF_8));
                    toClient.flush();
                }
                else
                {
                    String httpErrorResponse=handleError("Error Topic does not exist in graph!","Please check topic name.");
                    toClient.write(httpErrorResponse.getBytes(StandardCharsets.UTF_8));
                    toClient.flush();

                }
            }
            //file hasnt been uploaded
            else {
                String httpErrorResponse=handleError("Error No Configuration File!","Please upload configuration file first.");
                toClient.write(httpErrorResponse.getBytes(StandardCharsets.UTF_8));
                toClient.flush();

            }

    }

    @Override
    public void close() throws IOException {
    }
    private String handleError(String errorHeader, String errorMessage){
        String htmlResponse="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Error Page</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "        .error-container {\n" +
                "            background-color: #fff;\n" +
                "            padding: 20px;\n" +
                "            border: 1px solid #ccc;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .error-container h1 {\n" +
                "            font-size: 24px;\n" +
                "            color: red;\n" +
                "        }\n" +
                "        .error-container p {\n" +
                "            font-size: 16px;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"error-container\">\n" +
                "    <h1>"+errorHeader+"</h1>\n" +
                "    <p>"+errorMessage+"</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n";
        String httpResponse = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + htmlResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + htmlResponse;
        return httpResponse;

    }

    private boolean topicExists(String topic) {
        Collection<Topic> topics=TopicManagerSingleton.get().getTopics();
        for(Topic t:topics) {
            if (t.name.equals(topic))
                return true;
        }
        return false;
    }

}