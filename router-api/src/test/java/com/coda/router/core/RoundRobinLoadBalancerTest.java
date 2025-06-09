package com.coda.router.core;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinLoadBalancerTest {

    @Test
    void testRoundRobinOrder() throws Exception {
        var monitor = new InstanceHealthMonitor();
        var lb = new RoundRobinLoadBalancer(List.of(
                "http://localhost:5001",
                "http://localhost:5002",
                "http://localhost:5003"
        ), monitor);

        assertEquals("http://localhost:5001", lb.getNextHealthyInstance());
        assertEquals("http://localhost:5002", lb.getNextHealthyInstance());
        assertEquals("http://localhost:5003", lb.getNextHealthyInstance());
        assertEquals("http://localhost:5001", lb.getNextHealthyInstance());
    }

    @Test
    void testSkipsDegradedInstance() throws Exception {
        var monitor = new InstanceHealthMonitor();
        var lb = new RoundRobinLoadBalancer(List.of(
                "http://localhost:5001",
                "http://localhost:5002",
                "http://localhost:5003"
        ), monitor);

        monitor.reportResponseTime("http://localhost:5002", 2500);
        monitor.reportResponseTime("http://localhost:5002", 2500);

        assertEquals("http://localhost:5001", lb.getNextHealthyInstance());
        assertEquals("http://localhost:5003", lb.getNextHealthyInstance());
    }
}
