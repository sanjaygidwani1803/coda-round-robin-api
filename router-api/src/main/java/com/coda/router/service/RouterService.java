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
    private static final int MAX_RETRIES = 2;
    private final RoundRobinLoadBalancer loadBalancer;
    private final InstanceHealthMonitor monitor;
    private final HttpClient client;

    public RouterService(RoundRobinLoadBalancer loadBalancer, InstanceHealthMonitor monitor) {
        this.loadBalancer = loadBalancer;
        this.monitor = monitor;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public String forward(String jsonPayload) throws Exception {
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt ++) {
            String instance = loadBalancer.getNextHealthyInstance();
            System.out.println("[Router] Attempt " + attempt + ": Forwarding request to " + instance);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(instance))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            long start = System.currentTimeMillis();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                long duration = System.currentTimeMillis() - start;
                monitor.reportResponseTime(instance, duration);
                System.out.println("[Router] Received response from " + instance + " in " + duration + "ms: " + response.body());
                return response.body();
            } catch (IOException | InterruptedException e) {
                long duration = System.currentTimeMillis() - start;
                monitor.reportResponseTime(instance, 9999);
                System.err.println("[ERROR] Attempt " + attempt + " failed for " + instance + " (" + duration + "ms): " + e.getMessage());
                lastException = e;
            }
        }

        throw new IOException("All " + MAX_RETRIES + " attempts failed", lastException);
    }
}
