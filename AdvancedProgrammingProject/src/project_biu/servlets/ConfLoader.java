package project_biu.servlets;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import project_biu.view.HtmlGraphWriter;

import project_biu.configs.GenericConfig;
import project_biu.graph.Graph;
import project_biu.server.RequestParser.RequestInfo;

public class ConfLoader implements Servlet {
    static public GenericConfig gc=null;
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
                gc = new GenericConfig();
                gc.setConfFile("temp_config");
            try {
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

            } catch (Exception e) {
                String httpErrorResponse=handleError(e.getMessage());
                toClient.write(httpErrorResponse.getBytes(StandardCharsets.UTF_8));
                toClient.flush();

            }

        } else { // Handle error
            String errorHtml=handleError("Sorry, there was an issue reading the file.");
            // File path not provided
            String response = "HTTP/1.1 404 Bad Request\r\n\r\n"+
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + errorHtml.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Content-Disposition: inline; filename=\"graph.html\"; target=\"graphFrame\"\r\n" + // Ensure the correct target
                    "\r\n" +
                    errorHtml;

            toClient.write(response.getBytes(StandardCharsets.UTF_8));
            toClient.flush();
        }
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }

    private String handleError(String errorMessage){
        String htmlResponse="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Error Page</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "        .error-container {\n" +
                "            background-color: #fff;\n" +
                "            padding: 20px;\n" +
                "            border: 1px solid #ccc;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .error-container h1 {\n" +
                "            font-size: 24px;\n" +
                "            color: red;\n" +
                "        }\n" +
                "        .error-container p {\n" +
                "            font-size: 16px;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"error-container\">\n" +
                "    <h1>Error in configuration file</h1>\n" +
                "    <p>"+errorMessage+"</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n";
        String httpResponse = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + htmlResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" + htmlResponse;
        return httpResponse;

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
    }

}