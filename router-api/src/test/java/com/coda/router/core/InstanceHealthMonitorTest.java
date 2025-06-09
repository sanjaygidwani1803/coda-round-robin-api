package com.coda.router.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InstanceHealthMonitorTest {

    @Test
    void testDegradesAfterSlowResponsesAndRecovers() throws InterruptedException {
        var monitor = new InstanceHealthMonitor();
        String instance = "http://localhost:5001";

        monitor.reportResponseTime(instance, 2500);
        monitor.reportResponseTime(instance, 2500);

        assertTrue(monitor.isDegraded(instance));

        Thread.sleep(20500);

        assertFalse(monitor.isDegraded(instance));
    }

    @Test
    void testHealthyInstanceIsNotDegraded() {
        var monitor = new InstanceHealthMonitor();
        String instance = "http://localhost:5002";

        monitor.reportResponseTime(instance, 1500);
        monitor.reportResponseTime(instance, 1000);
        monitor.reportResponseTime(instance, 1900);

        assertFalse(monitor.isDegraded(instance));
    }
}
