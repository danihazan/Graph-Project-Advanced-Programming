package project_biu.server;


import project_biu.servlets.Servlet;

/**
 * The HTTPServer interface defines methods for managing servlets and handling HTTP requests.
 * It extends the Runnable interface, allowing the server to run in a separate thread.
 */
public interface HTTPServer extends Runnable{

    /**
     * Adds a servlet to handle requests for the specified HTTP command and URI.
     *
     * @param "httpCommand" the HTTP command (e.g., GET, POST) that the servlet will handle
     * @param uri the URI for which the servlet will handle requests
     * @param s the servlet to be added
     */
    public void addServlet(String httpCommanmd, String uri, Servlet s);

    /**
     * Removes the servlet that handles requests for the specified HTTP command and URI.
     *
     * @param "httpCommand" the HTTP command (e.g., GET, POST) for which the servlet will be removed
     * @param uri the URI for which the servlet will be removed
     */
    public void removeServlet(String httpCommanmd, String uri);


    /**
     * Starts the HTTP server, beginning to accept and handle requests.
     */
    public void start();


    /**
     * Closes the HTTP server, performing any necessary cleanup and stopping it from accepting new requests.
     */
    public void close();
}
