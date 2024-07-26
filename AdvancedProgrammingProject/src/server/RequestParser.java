package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }

        //Handle first line: Command + URI + HTTP Version
        String[] requestParts = requestLine.split(" ");
        if(requestParts.length<2)
            throw new IOException("Invalid request missing Command/ URI");
        
        String command = requestParts[0]; //command
        String uri = requestParts[1];//uri

        //Divide Uri to path and parameters using char '?'
        String[] uriParts = uri.split("\\?");
        //Handle uri path to segments
        String path = uriParts[0];
        String[] rawPathSegments = path.split("/");
        // Filter out empty segments
        List<String> filteredPathSegments = new ArrayList<>();
        for (String segment : rawPathSegments) {
            if (!segment.isEmpty()) {
                filteredPathSegments.add(segment);
            }
        }
        String[] pathSegments = filteredPathSegments.toArray(new String[0]);
        //Handle uri parameters
        Map<String, String> queryParams = new HashMap<>();
        if (uriParts.length > 1) {
            handleParameters(uriParts[1],queryParams);
        }

        // Read the headers
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // Read the body (if any)
        int contentLength = 0;
        if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        }
        byte[] bodyContent = new byte[contentLength];
        if (contentLength > 0) {
            int bytesRead = 0;
            while (bytesRead < contentLength) {
                Readable byteArrayInputStream;
                int result = byteArrayInputStream.read(bodyContent, bytesRead, contentLength - bytesRead);
                if (result == -1) {
                    break;
                }
                bytesRead += result;
            }
        }

        // Create and return the RequestInfo object

        // Create and return the RequestInfo object

        System.out.println("command:"+command);
        System.out.println("uri:"+uri);
        System.out.println("Path:"+pathSegments);
        System.out.println("parameters:"+queryParams);
        System.out.println("Content:"+body.toString().getBytes(StandardCharsets.UTF_8));
        return new RequestInfo(command, uri, pathSegments, queryParams, bodyContent);


    }
    private static void handleParameters(String paramString, Map<String, String> parameters) {
        String[] params = paramString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length > 1) {
                parameters.put(keyValue[0], keyValue[1]);
            } else {
                parameters.put(keyValue[0], "");
            }
        }
    }



    // RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
        public void PrintRequest(){
            System.out.println("http command:"+httpCommand);
            System.out.println("uri"+uri);
            System.out.println("uri segments:"+Arrays.toString(uriSegments));
            System.out.println("parameters:"+parameters);
            System.out.println("content:"+content.toString());

        }
    }
}
