package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import server.RequestParser.RequestInfo;

public class HtmlLoader implements Servlet {
    private String htmlFolder;

    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        String filePath = htmlFolder + requestInfo.getUri().substring(4); // Remove "/app"
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + content;
            toClient.write(response.getBytes());
        } catch (IOException e) {
            String response = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
            toClient.write(response.getBytes());
        }
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }
}
