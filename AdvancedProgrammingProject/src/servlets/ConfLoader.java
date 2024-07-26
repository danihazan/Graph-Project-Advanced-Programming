package servlets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import view.HtmlGraphWriter;

import configs.GenericConfig;
import graph.Graph;
import server.RequestParser.RequestInfo;

public class ConfLoader implements Servlet {
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        String data=readFile(requestInfo);
        System.out.println("File Data: \n"+data);

        // Extract the uploaded file part from the request
        Map<String, String> parameters = requestInfo.getParameters();
        String filePath = parameters.get("configFilePath");

        if (filePath != null) {
            // Ensure the file exists
            File configFile = new File(filePath);
            if (configFile.exists()) {
                // Read the configuration file content
                StringBuilder fileContentBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContentBuilder.append(line).append("\n");
                    }
                }


                //Creating Agents and topics from config file using Generic config
                GenericConfig gc = new GenericConfig();
                gc.setConfFile(filePath);
                gc.create();
                //Create Graph of Agents and topic uploaded
                Graph graphFromConfig=new Graph();
                graphFromConfig.createFromTopics();
                String graphHtml= HtmlGraphWriter.getGraphHTML(graphFromConfig);

                // Create Http response
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + graphHtml.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" +
                        graphHtml;
                toClient.write(httpResponse.getBytes());
            } else {
                // File not found
                String response = "HTTP/1.1 404 Not Found\r\n\r\nConfiguration file not found.";
                toClient.write(response.getBytes());
            }
        } else { // Handle error not handled
            // File path not provided
            String response = "HTTP/1.1 400 Bad Request\r\n\r\nConfiguration file path not provided.";
            toClient.write(response.getBytes());
        }
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }

    public String createErrorHttpResponse(String errorMessage) {
        StringBuilder response = new StringBuilder();

        return response.toString();
    }


    public String readFile(RequestInfo requestInfo) throws IOException {
        // Extract the content type boundary
        System.out.println(requestInfo.getParameters().containsKey("Content-Disposition: form-data"));
        String contentType = requestInfo.getParameters().get("Content-Disposition: form-data");
        System.out.println(contentType);

        if (contentType == null || !contentType.contains("multipart/form-data")) {
            throw new IOException("Content-Type must be multipart/form-data");
        }

        String boundary = contentType.split("boundary=")[1];
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);

        // Split content into parts using the boundary
        ByteArrayInputStream contentStream = new ByteArrayInputStream(requestInfo.getContent());
        BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8));
        ByteArrayOutputStream fileContent = new ByteArrayOutputStream();
        String line;
        boolean filePart = false;
        String fileName = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("--" + boundary)) {
                if (filePart && fileName != null) {
                    // Process the file part in-memory and return the content as a string
                    return fileContent.toString(StandardCharsets.UTF_8.name());
                }
            } else if (line.startsWith("Content-Disposition: form-data;")) {
                Pattern filePattern = Pattern.compile("filename=\"(.*?)\"");
                Matcher fileMatcher = filePattern.matcher(line);

                if (fileMatcher.find()) {
                    fileName = fileMatcher.group(1);
                    filePart = true;
                }
            } else if (filePart) {
                fileContent.write(line.getBytes(StandardCharsets.UTF_8));
                fileContent.write("\r\n".getBytes(StandardCharsets.UTF_8));  // Preserve line breaks
            }
        }

        if (filePart && fileName != null) {
            // Process any remaining file part in-memory and return the content as a string
            return fileContent.toString(StandardCharsets.UTF_8.name());
        }

        return null;  // No file content found
    }

}