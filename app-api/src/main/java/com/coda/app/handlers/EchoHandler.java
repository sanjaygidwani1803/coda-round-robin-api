package com.coda.app.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class EchoHandler implements HttpHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            InputStream inputStream = exchange.getRequestBody();
            Object json = objectMapper.readValue(inputStream, Object.class);
            byte[] responseBytes = objectMapper.writeValueAsBytes(json);
            System.out.println("[App] Request: " + objectMapper.writeValueAsString(json));

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(responseBytes);
            outputStream.close();
        } catch (Exception e) {
            try {
                String error = "{\"error\": \"Invalid JSON\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, error.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(error.getBytes(StandardCharsets.UTF_8));
                exchange.getResponseBody().close();
            } catch (IOException ignored) {}
        }
    }
}
