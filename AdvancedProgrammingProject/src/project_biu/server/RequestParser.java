package project_biu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The RequestParser class provides functionality to parse HTTP requests from a BufferedReader.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from the provided BufferedReader.
     *
     * @param reader the BufferedReader to read the HTTP request from
     * @return a RequestInfo object containing the parsed request information
     * @throws IOException if an I/O error occurs or the request is invalid
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new IOException("Invalid/Empty request line");
        }

        // Parse the request line
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            throw new IOException("Invalid request missing Command/ URI");
        }
        String command = requestParts[0];
        String uri = requestParts[1];

        // Split URI into path and query parameters
        String[] uriComponents = uri.split("\\?", 2);
        String path = uriComponents[0];
        String[] pathSegments = Arrays.stream(path.split("/"))
                .filter(segment -> !segment.isEmpty())
                .toArray(String[]::new);

        // Extract query parameters if present
        Map<String, String> queryParams = new LinkedHashMap<>();
        if (uriComponents.length > 1) {
            parseQueryParams(uriComponents[1], queryParams);
        }


        // Read headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        // ADDED - START
        /*
        Checking for additional parameters like fileName and others by looking for '=' signs,
        First marking the state of the reader so we can go back if there was no additional parameters.
        we do that so we can get the pointer of the BufferReader to the exact spot where the content start.

        we have 2 situations to cover:
        1. GET/POST \n ":" parameters \n content \n
        2. GET/POST \n ":" parameters \n '=' parameters \n content \n
         */
        reader.mark(2048); // The additional parameters size are unlikely to pass that limit
        boolean extraParameters = false;
        try {
            while ((line = reader.readLine()) != null && line.trim().contains("=")) {
                extraParameters = true;
                String[] headerParts = line.split("=", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
            }

        }
        catch (IOException e) {
            // if there wasn't parameters we need to revert the pointer to the start of content data
            if (!extraParameters) {
                reader.reset();
            }
        }

        // ADDED - END

        // Determine content length and read the body
        int contentLength = headers.containsKey("Content-Length")
                ? Integer.parseInt(headers.get("Content-Length"))
                : 0;

        StringBuilder bodyBuilder = new StringBuilder();

        if (contentLength > 0) {
            char[] bodyBuffer = new char[contentLength];
            int bytesRead = reader.read(bodyBuffer, 0, contentLength);
            if (bytesRead == -1) {
                throw new IOException("Content body is empty.");
            }
            bodyBuilder.append(bodyBuffer);
        }

        if(reader.ready()) {
            System.err.println("Warning: The size contentLength that was given is smaller then the actual content, there is more content to read.");
        }

        byte[] contentBytes = bodyBuilder.toString().getBytes(StandardCharsets.UTF_8);

        // Print request details for debugging
//        debugPrint(command, uri, pathSegments, queryParams, contentBytes);
        return new RequestInfo(command, uri, pathSegments, queryParams, headers, contentBytes);
    }

    /**
     * Helper function - Parses query parameters from a query string.
     *
     * @param queryString the query string to parse
     * @param queryParams the map to store the parsed query parameters
     */
    private static void parseQueryParams(String queryString, Map<String, String> queryParams) {
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue.length > 1 ? keyValue[1].trim() : "";
            queryParams.put(key, value);
        }
    }

    /**
     * Debugging method to print request details.
     *
     * @param command the HTTP command
     * @param uri the URI
     * @param pathSegments the URI path segments
     * @param queryParams the query parameters
     * @param contentBytes the request content bytes
     */
    private static void debugPrint(String command, String uri, String[] pathSegments, Map<String, String> queryParams, byte[] contentBytes) {
        System.out.println("Command: " + command);
        System.out.println("URI: " + uri);
        System.out.println("Path Segments: " + Arrays.toString(pathSegments));
        System.out.println("Query Parameters: " + queryParams);
        System.out.println("---");
        System.out.println("Content: " + new String(contentBytes, StandardCharsets.UTF_8));
        System.out.println("---");
    }

    /**
     * The RequestInfo class encapsulates information about an HTTP request.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final Map<String, String> headers;
        private final byte[] content;

        /**
         * Constructs a new RequestInfo object with the specified details.
         *
         * @param httpCommand the HTTP command
         * @param uri the URI
         * @param uriSegments the URI path segments
         * @param parameters the query parameters
         * @param headers the headers
         * @param content the content
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, Map<String, String> headers, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.headers = headers;
            this.content = content;
        }

        /**
         * Returns the HTTP command.
         *
         * @return the HTTP command
         */
        public String getHttpCommand() {
            return httpCommand;
        }

        /**
         * Returns the URI.
         *
         * @return the URI
         */
        public String getUri() {
            return uri;
        }

        /**
         * Returns the URI segments.
         *
         * @return the URI segments
         */
        public String[] getUriSegments() {
            return uriSegments;
        }


        /**
         * Returns the query parameters.
         *
         * @return the query parameters
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Returns the headers.
         *
         * @return the headers
         */
        public Map<String, String> getHeaders() {
            return headers;
        }
        /**
         * Returns the content.
         *
         * @return the content
         */
        public byte[] getContent() {
            return content;
        }

        /**
         * Debugging method to print request details.
         */
        public void printRequest() {
            System.out.println("HTTP Command: " + httpCommand);
            System.out.println("URI: " + uri);
            System.out.println("URI Segments: " + Arrays.toString(uriSegments));
            System.out.println("Parameters: " + parameters);
            System.out.println("Headers: " + headers);
            System.out.println("Content: " + new String(content, StandardCharsets.UTF_8));
        }
    }
}
