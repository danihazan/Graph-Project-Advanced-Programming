package project_biu.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import project_biu.configs.BinOpAgent;
import project_biu.configs.Config;
import project_biu.configs.MathExampleConfig;
import project_biu.configs.Node;
import project_biu.graph.Agent;
import project_biu.graph.Graph;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;
import project_biu.graph.TopicManagerSingleton.TopicManager;

public class GraphTest {

    public static void testCreateFromTopicsandCycles()
    {
        BinOpAgent g1= new BinOpAgent("agent1", "A", "B", "C", (x, y)->x+y);
        BinOpAgent g2= new BinOpAgent("agent2", "C", "D", "E", (x, y)->x+y);
        BinOpAgent g3= new BinOpAgent("agent3", "C", "D", "F", (x, y)->x+y);
        BinOpAgent g4=new BinOpAgent("agent4", "E", "F", "G", (x, y)->x+y);
        Graph g=new Graph();
        g.createFromTopics();
        for(Node node:g)
        {
            node.printAllNeighbours();
        }
        System.out.println("Cycles in Graph before:"+g.hasCycles());
        BinOpAgent g5=new BinOpAgent("agent5", "F", "G", "A", (x, y)->x+y);
        g.createFromTopics();
        System.out.println("Cycles in Graph after:"+g.hasCycles());


    }

    public static boolean hasCycles(List<Node> graph) {
        for (Node node : graph) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }

    public static void testCycles(){
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");

        a.addEdge(b);
        b.addEdge(c);
        c.addEdge(d);

        // Create a graph
        List<Node> graph = new ArrayList<>();
        graph.add(a);
        graph.add(b);
        graph.add(c);
        graph.add(d);

        // Check if the graph has cycles
        boolean hasCycles = hasCycles(graph);
        if(hasCycles)
            System.out.println("wrong answer for hasCycles when there are no cycles (-20)");

        d.addEdge(a);
        hasCycles = hasCycles(graph);
        if(!hasCycles)
            System.out.println("wrong answer for hasCycles when there is a cycle (-10)");

    }

    public static class GetAgent implements Agent {

        public Message msg;
        public GetAgent(String topic){
            TopicManagerSingleton.get().getTopic(topic).subscribe(this);
        }

        @Override
        public String getName() { return "Get Agent";}

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            this.msg=msg;
        }

        @Override
        public void close() {}

    }

    public static void testBinGraph(){
        TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c=new MathExampleConfig();
        try {
            c.create();
        }
        catch (Exception e) {}

        GetAgent ga=new GetAgent("R3");

        Random r=new Random();
        int x=1+r.nextInt(100);
        int y=1+r.nextInt(100);
        tm.getTopic("A").publish(new Message(x));
        tm.getTopic("B").publish(new Message(y));
        double rslt=(x+y)*(x-y);

        if (Math.abs(rslt - ga.msg.asDouble)>0.05)
            System.out.println("your BinOpAgents did not produce the desired result (-20)");


    }

    public static void testTopicsGraph(){
        TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c=new MathExampleConfig();
        try {
            c.create();
        }
        catch (Exception e) {}

        Graph g=new Graph();
        g.createFromTopics();
        for(Node node:g)
        {
            System.out.println(node.getName());
        }

        if(g.size()!=8)
            System.out.println("the graph you created from topics is not in the right size (-10)");

        List<String> l=Arrays.asList("TA","TB","Aplus","Aminus","TR1","TR2","Amul","TR3");
        boolean b=true;
        for(Node n  : g){
            b&=l.contains(n.getName());
        }
        if(!b)
            System.out.println("the graph you created from topics has wrong names to Nodes (-10)");

        if (g.hasCycles())
            System.out.println("Wrong result in hasCycles for topics graph without cycles (-10)");

        GetAgent ga=new GetAgent("R3");
        tm.getTopic("A").addPublisher(ga); // cycle
        g.createFromTopics();

        if (!g.hasCycles())
            System.out.println("Wrong result in hasCycles for topics graph with a cycle (-10)");
    }

    public static void graphTestMain(){
        testCycles();
        testBinGraph();
        testTopicsGraph();
        System.out.println("done");
    }

}