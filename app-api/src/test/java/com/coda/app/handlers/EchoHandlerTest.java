package com.coda.app.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class EchoHandlerTest {

    @Test
    void testHandleValidJson() throws IOException {
        EchoHandler handler = new EchoHandler();

        String inputJson = "{\"test\":\"data\"}";
        HttpExchange exchange = mock(HttpExchange.class);
        InputStream is = new ByteArrayInputStream(inputJson.getBytes(StandardCharsets.UTF_8));
        OutputStream os = new ByteArrayOutputStream();
        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(is);
        when(exchange.getResponseBody()).thenReturn(os);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
    }

    @Test
    void testHandleInvalidJson() throws IOException {
        EchoHandler handler = new EchoHandler();

        String inputJson = "{not:json}";
        HttpExchange exchange = mock(HttpExchange.class);
        InputStream is = new ByteArrayInputStream(inputJson.getBytes(StandardCharsets.UTF_8));
        OutputStream os = new ByteArrayOutputStream();
        Headers headers = new Headers();
        when(exchange.getResponseHeaders()).thenReturn(headers);

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(is);
        when(exchange.getResponseBody()).thenReturn(os);

        handler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(400), anyLong());
    }
}
