package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;

import project_biu.server.RequestParser.RequestInfo;

/**
 * The Servlet interface defines methods that all servlets must implement for handling requests and managing resources.
 * Servlets help the server delegate the handling of requests to the appropriate servlet to generate dynamic content
 * or perform processing tasks.
 */
public interface Servlet {

    /**
     * Handles the incoming request and writes the response to the provided OutputStream.
     *
     * @param ri the RequestInfo object containing details about the client's request
     * @param toClient the OutputStream to which the response will be written
     * @throws IOException if an input or output exception occurs
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes the resource, performing any necessary cleanup.
     *
     * @throws IOException if an input or output exception occurs
     */
    void close() throws IOException;
}
