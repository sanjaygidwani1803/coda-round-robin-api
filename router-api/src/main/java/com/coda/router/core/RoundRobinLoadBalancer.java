package com.coda.router.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer {
    private final List<String> instances;
    private final InstanceHealthMonitor monitor;
    private final AtomicInteger index = new AtomicInteger(0);

    public RoundRobinLoadBalancer(List<String> instances, InstanceHealthMonitor monitor) {
        this.instances = instances;
        this.monitor = monitor;
    }

    public String getNextHealthyInstance() throws Exception {
        int total = instances.size();
        for (int i = 0; i < total; i ++) {
            int current = index.getAndUpdate(n -> (n + 1) % total);
            String candidate = instances.get(current);
            if (!monitor.isDegraded(candidate)) {
                return candidate;
            }
        }
        throw new Exception("No healthy instances available");
    }
}
