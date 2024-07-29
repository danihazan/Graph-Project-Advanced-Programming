package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import project_biu.server.RequestParser.RequestInfo;

/**
 * The HtmlLoader class is a servlet responsible for loading and serving HTML content.
 * Handle "GET", "/app/" requests
 */
public class HtmlLoader implements Servlet {
    private final String htmlFolder;
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("js", "application/javascript");
    }

    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

    /**
     * Handles the incoming HTTP request, serving the requested HTML file or returning an appropriate error message.
     *
     * @param requestInfo the RequestInfo object containing details about the client's request
     * @param toClient the OutputStream to which the response will be written
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        String modifiedPath = htmlFolder + requestInfo.getUri().substring(4); // Remove "/app"
        Path filePath = Paths.get(modifiedPath).normalize();

        if (!filePath.startsWith(Paths.get(htmlFolder))) {
            String response = "HTTP/1.1 403 Forbidden\r\n\r\nAccess Denied";
            toClient.write(response.getBytes());
            return;
        }
        try {
            byte[] content = Files.readAllBytes(filePath);
            String contentType = MIME_TYPES.getOrDefault(getFileExtension(filePath), "text/plain");
            String response = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\n\r\n";
            toClient.write(response.getBytes());
            toClient.write(content);
        } catch (IOException e) {
            String response = "HTTP/1.1 404 Not Found\r\n\r\nFile Not Found";
            toClient.write(response.getBytes());
        }
    }

    /**
     * Helper method that retrieves the file extension of the given file path.
     *
     * @param path the path of the file
     * @return the file extension as a string
     */
    private String getFileExtension(Path path) {
        String name = path.toString();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1);
    }

    /**
     * Closes the resource, performing any necessary cleanup.
     *
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }
}