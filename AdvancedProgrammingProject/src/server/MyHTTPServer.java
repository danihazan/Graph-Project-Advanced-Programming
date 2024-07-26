package server;

import servlets.Servlet;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port;
    private final ExecutorService threadPool;
    private final ConcurrentHashMap<String, Servlet> getServlets;
    private final ConcurrentHashMap<String, Servlet> postServlets;
    private final ConcurrentHashMap<String, Servlet> deleteServlets;
    private final Lock lock;
    private volatile boolean running = true;

    /**
     * Constructor to initialize the server with a port and maximum number of threads.
     * @param port The port number on which the server listens.
     * @param nThreads The maximum number of threads in the thread pool.
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
        this.getServlets = new ConcurrentHashMap<>();
        this.postServlets = new ConcurrentHashMap<>();
        this.deleteServlets = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Adds a servlet to handle requests for a specific HTTP method and URI.
     * @param httpCommand The HTTP method (GET, POST, DELETE).
     * @param uri The URI to be handled by the servlet.
     * @param s The servlet to handle the requests.
     */
    public void addServlet(String httpCommand, String uri, Servlet s) {
        lock.lock();
        try {
            switch (httpCommand.toUpperCase()) {
                case "GET":
                    getServlets.put(uri, s);
                    break;
                case "POST":
                    postServlets.put(uri, s);
                    break;
                case "DELETE":
                    deleteServlets.put(uri, s);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a servlet for a specific HTTP method and URI.
     * @param httpCommand The HTTP method (GET, POST, DELETE).
     * @param uri The URI from which the servlet should be removed.
     */
    public void removeServlet(String httpCommand, String uri) {
        lock.lock();
        try {
            switch (httpCommand.toUpperCase()) {
                case "GET":
                    getServlets.remove(uri);
                    break;
                case "POST":
                    postServlets.remove(uri);
                    break;
                case "DELETE":
                    deleteServlets.remove(uri);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP command: " + httpCommand);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Starts the server in a new thread.
     */
    @Override
    public void start() {
        new Thread(this).start();
    }

    /**
     * Main server loop. Listens for client connections and handles them using a thread pool.
     */
    @Override
    public void run() {
        // listening socket to port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(1000);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(6000);
                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (SocketTimeoutException e) {
                    // Timeout occurred, loop continues to check for shutdown or new connections
                } catch (IOException e) {
                    if (!running) {
                        break; // Exit loop if server is stopped
                    }
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the server by setting the running flag to false and shutting down the thread pool.
     * Closes the server socket if it is open.
     */
    public void close() {
        running = false;
        threadPool.shutdown();
    }

    /**
     * Handles an individual client request.
     * @param clientSocket The socket connected to the client.
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(in);
            String httpCommand = requestInfo.getHttpCommand();
            String uri = requestInfo.getUri();

            Servlet servlet = null;
            switch (httpCommand.toUpperCase()) {
                case "GET":
                    servlet = findLongestMatchingServlet(uri, getServlets);
                    break;
                case "POST":
                    servlet = findLongestMatchingServlet(uri, postServlets);
                    break;
                case "DELETE":
                    servlet = findLongestMatchingServlet(uri, deleteServlets);
                    break;
                default:
                    out.write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes());
                    return;
            }

            if (servlet != null) {
                servlet.handle(requestInfo, out);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Finds the servlet with the longest matching URI prefix.
     * @param uri The URI requested by the client.
     * @param servlets The map of URIs to servlets.
     * @return The servlet that handles the URI, or null if no matching servlet is found.
     */
    private Servlet findLongestMatchingServlet(String uri, ConcurrentHashMap<String, Servlet> servlets) {
        Servlet matchedServlet = null;
        int maxMatchLength = -1;

        for (String keyUri : servlets.keySet()) {
            if (uri.startsWith(keyUri) && keyUri.length() > maxMatchLength) {
                matchedServlet = servlets.get(keyUri);
                maxMatchLength = keyUri.length();
            }
        }

        return matchedServlet;
    }
}
