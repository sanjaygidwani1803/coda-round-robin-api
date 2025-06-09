package com.coda.router.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceHealthMonitor {
    private static final int FAILURE_LIMIT = 2;
    private static final long DEGRADATION_THRESHOLD_MS = 2000;
    private static final long COOLDOWN_MS = 20000;

    private final Map<String, AtomicInteger> slowCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> degradedUntil = new ConcurrentHashMap<>();

    public void reportResponseTime(String instance, long durationMs) {
        if (durationMs > DEGRADATION_THRESHOLD_MS) {
            slowCounts.computeIfAbsent(instance, k -> new AtomicInteger(0)).incrementAndGet();
            System.out.println("[Monitor] Slow response from " + instance + " (" + durationMs + "ms)");

            if (slowCounts.get(instance).get() >= FAILURE_LIMIT) {
                degradedUntil.put(instance, System.currentTimeMillis() + COOLDOWN_MS);
                System.out.println("[Monitor] Marked " + instance + " as degraded for " + COOLDOWN_MS + "ms");
            }
        } else {
            slowCounts.computeIfAbsent(instance, k -> new AtomicInteger(0)).set(0);
        }
    }

    public boolean isDegraded(String instance) {
        Long until = degradedUntil.get(instance);
        if (until == null) {
            return false;
        }
        if (System.currentTimeMillis() > until) {
            degradedUntil.remove(instance);
            slowCounts.computeIfAbsent(instance, k -> new AtomicInteger(0)).set(0);
            return false;
        }
        return true;
    }
}
