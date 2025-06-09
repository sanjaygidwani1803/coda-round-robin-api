package com.coda.app;

import com.coda.app.handlers.EchoHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AppServer {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getProperty("PORT", "5001"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new EchoHandler());
        server.setExecutor(null);
        System.out.println("App API running on port " + port);
        server.start();
    }
}
