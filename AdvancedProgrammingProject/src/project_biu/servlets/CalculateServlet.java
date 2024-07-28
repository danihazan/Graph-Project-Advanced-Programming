package project_biu.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import project_biu.server.RequestParser.RequestInfo;
/**
 * The CalculateServlet class handles HTTP requests and performs calculations based on the provided parameters.
 */
public class CalculateServlet implements Servlet {

    /**
     * Handles the incoming request and writes the response to the provided OutputStream.
     *
     * @param requestInfo the RequestInfo object containing details about the client's request
     * @param response the OutputStream to which the response will be written
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream response) throws IOException {
        System.out.println("CalculateServlet HANDLE");
        Map<String, String> parameters = requestInfo.getParameters();
        int a = Integer.parseInt(parameters.getOrDefault("a", "0"));
        int b = Integer.parseInt(parameters.getOrDefault("b", "0"));
        String operation = parameters.getOrDefault("op", "subtract");
        int result;
        System.out.println("a:"+a+" b:"+b+" operation:"+operation);

        switch (operation) {
            case "add":
                result = a + b;
                break;
            case "multiply":
                result = a * b;
                break;
            case "divide":
                if (b != 0) {
                    result = a / b;
                } else {
                    result = 0; // Handle division by zero
                }
                break;
            case "subtract":
            default:
                result = a - b;
                break;
        }

        String resultString = "Result: " + result;
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + resultString.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                resultString;
        response.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        response.flush();
    }

    @Override
    public void close() throws IOException {
        // No resources to close in this implementation
    }
}
