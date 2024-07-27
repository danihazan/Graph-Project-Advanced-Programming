package servlets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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
        BufferedReader reader = new BufferedReader(new StringReader(data));
        Map<String, String> content_params = new HashMap<>(Map.of());
        String line;
        String filePath = null;

        while ((line = reader.readLine()) != null && line.trim().contains(":")) {
            if (line.contains(";")) {
                String[] paramParts = line.split(";");
                for (String paramPart : paramParts) {
                    String split_param = "";
                    if (paramPart.contains(":")) {
                        split_param = ":";
                    }
                    else {
                        split_param = "=";
                    }
                    String[] key_val = paramPart.split(split_param, 2);
                    String key = key_val[0];
                    String val = key_val[1];
                    content_params.put(key, val);
                    if (key.contains("filename")) {
                        filePath = val;
                    }
                }
            } else {
                String[] key_val = line.split(":", 2);
                String key = key_val[0];
                String val = key_val[1];
                content_params.put(key, val);
            }
        }

        // Get file content
        StringBuilder conf_data = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            conf_data.append(line);
            conf_data.append("\n");
        }

        // Save file content in a temp file
        Path tempFilePath = Paths.get(("temp_config"));
        try {
            Files.writeString(tempFilePath, conf_data.toString());
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        }

        if (filePath != null) {
                //Creating Agents and topics from config file using Generic config
                GenericConfig gc = new GenericConfig();
                gc.setConfFile("temp_config");
                gc.create();
                //Create Graph of Agents and topic uploaded
                Graph graphFromConfig=new Graph();
                graphFromConfig.createFromTopics();
                String graphHtml= HtmlGraphWriter.getGraphHTML(graphFromConfig);

                // Create Http response
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + graphHtml.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "Content-Disposition: inline; filename=\"graph.html\"; target=\"graphFrame\"\r\n" + // Ensure the correct target
                        "\r\n" +
                        graphHtml;
                toClient.write(httpResponse.getBytes());

                // remove temp. file
                Files.delete(tempFilePath);

        } else { // Handle error
            // File path not provided
            String response = "HTTP/1.1 404 Bad Request\r\n\r\nConfiguration file path not provided.";
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
        String contentType = requestInfo.getHeaders().get("Content-Type");

        if (contentType == null || !contentType.contains("multipart/form-data")) {
            throw new IOException("Content-Type must be multipart/form-data");
        }

        String boundary = contentType.split("boundary=")[1];

        // Split content into parts using the boundary
        ByteArrayInputStream contentStream = new ByteArrayInputStream(requestInfo.getContent());
        BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8));
        String line;
        StringBuilder filteredFileContent = new StringBuilder();


        while ((line = reader.readLine()) != null) {
            if (line.contains(boundary)) {
                break; // Skipping boundary part
            }
            filteredFileContent.append(line).append("\n");
        }

        return filteredFileContent.toString();
//        while ((line = reader.readLine()) != null) {
//            if (line.startsWith("----------------------------" + boundary)) {
////                if (filePart && fileName != null) {
//                // Process the file part in-memory and return the content as a string
//                return fileContent.toString().toString(StandardCharsets.UTF_8);
////                }
//            } else if (line.startsWith("Content-Disposition: form-data;")) {
//                Pattern filePattern = Pattern.compile("filename=\"(.*?)\"");
//                Matcher fileMatcher = filePattern.matcher(line);
//
//                if (fileMatcher.find()) {
//                    fileName = fileMatcher.group(1);
//                    filePart = true;
//                }
//            if (filePart) {
//                fileContent.toString().write(line.getBytes(StandardCharsets.UTF_8));
//                fileContent.toString().write("\r\n".getBytes(StandardCharsets.UTF_8));  // Preserve line breaks
//            }
//        }
//
//        if (filePart && fileName != null) {
//            // Process any remaining file part in-memory and return the content as a string
//            System.out.println("The content is - \n" + fileContent.toString().toString(StandardCharsets.UTF_8));
//            return fileContent.toString().toString(StandardCharsets.UTF_8);
//        }

//        return null;  // No file content found
    }

}