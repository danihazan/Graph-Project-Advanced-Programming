package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;

import project_biu.server.RequestParser.RequestInfo;

public interface Servlet {

    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}
