import configs.IncAgent;
import configs.Node;
import configs.PlusAgent;
import graph.Graph;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.Message;
import server.HTTPServer;
import server.MyHTTPServer;
import server.RequestParser;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.TopicDisplayer;
import view.HtmlGraphWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {


//        Graph graph = new Graph();
//        TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();
//        TopicManagerSingleton.get().getTopic("A");
//        TopicManagerSingleton.get().getTopic("B");
//        TopicManagerSingleton.get().getTopic("C");
//        IncAgent incAgent=new IncAgent(new String[]{"A"}, new String[]{"B"});
//        PlusAgent plusAgent=new PlusAgent(new String[]{"B","C"}, new String[]{"D"});
//        tm.getTopic("A").publish(new Message("1"));
//        tm.getTopic("C").publish(new Message("1"));
//        Graph g=new Graph();
//        g.createFromTopics();
        HTTPServer server=new MyHTTPServer(8080,5);
        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader());
        server.start();
//        System.in.read();
//        server.close();

        /*
        for(Node node:g){
            System.out.println("Node: " + node.getName());
            node.setMsg(new Message("1"));
        }
        try {
            String graphHtml = HtmlGraphWriter.getGraphHTML(g);
            System.out.println(graphHtml); // You can write this to an HTML file or serve it dynamically
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
