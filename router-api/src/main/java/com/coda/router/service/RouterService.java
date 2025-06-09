package com.coda.router.service;

import com.coda.router.core.InstanceHealthMonitor;
import com.coda.router.core.RoundRobinLoadBalancer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RouterService {
    private final RoundRobinLoadBalancer loadBalancer;
    private final InstanceHealthMonitor monitor;
    private final HttpClient client;

    public RouterService(RoundRobinLoadBalancer loadBalancer, InstanceHealthMonitor monitor) {
        this.loadBalancer = loadBalancer;
        this.monitor = monitor;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public String forward(String jsonPayload) throws Exception {
        String instance = loadBalancer.getNextHealthyInstance();
        System.out.println("[Router] Forwarding request to: " + instance);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(instance))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        long start = System.currentTimeMillis();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            monitor.reportResponseTime(instance, 9999);
            throw new IOException("Request to " + instance + " failed", e);
        }
        long duration = System.currentTimeMillis() - start;
        monitor.reportResponseTime(instance, duration);
        System.out.println("[INFO] Received response from " + instance + " in " + duration + "ms: " + response.body());

        return response.body();
    }
}
