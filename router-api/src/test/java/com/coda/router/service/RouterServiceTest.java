package com.coda.router.service;

import com.coda.router.core.InstanceHealthMonitor;
import com.coda.router.core.RoundRobinLoadBalancer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouterServiceTest {

    @Test
    void testForwardFailsWhenTargetIsDown() {
        var monitor = new InstanceHealthMonitor();
        var lb = new RoundRobinLoadBalancer(List.of("http://localhost:9999"), monitor);
        var service = new RouterService(lb, monitor);

        Exception ex = assertThrows(Exception.class, () ->
                service.forward("{\"test\":\"data\"}")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("failed"));
    }

    @Test
    void testForwardFailsOnMalformedJson() {
        var monitor = new InstanceHealthMonitor();
        var lb = new RoundRobinLoadBalancer(List.of("http://localhost:9999"), monitor);
        var service = new RouterService(lb, monitor);

        Exception ex = assertThrows(Exception.class, () ->
                service.forward("{not:json}")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("failed"));
    }
}
