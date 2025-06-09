package com.coda.router.api;

import com.coda.router.service.RouterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RouterHttpHandler implements HttpHandler {

    private final RouterService routerService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RouterHttpHandler(RouterService routerService) {
        this.routerService = routerService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            InputStream is = exchange.getRequestBody();
            Object payload = objectMapper.readValue(is, Object.class);
            String json = objectMapper.writeValueAsString(payload);
            System.out.println("[Router] Received JSON: " + json);

            String response = routerService.forward(json);
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        } catch (Exception e) {
            try {
                String errorJson = "{\"error\": \"Invalid JSON\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, errorJson.getBytes(StandardCharsets.UTF_8).length);
                exchange.getResponseBody().write(errorJson.getBytes(StandardCharsets.UTF_8));
                exchange.getResponseBody().close();
            } catch (IOException ignored) {}
        }
    }
}
