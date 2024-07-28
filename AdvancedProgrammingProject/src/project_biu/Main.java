package project_biu;

import project_biu.server.HTTPServer;
import project_biu.server.MyHTTPServer;
import project_biu.servlets.ConfLoader;
import project_biu.servlets.FeaturesHandler;
import project_biu.servlets.TopicDisplayer;

import java.io.IOException;

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
        server.addServlet("GET","/features", new FeaturesHandler());
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
