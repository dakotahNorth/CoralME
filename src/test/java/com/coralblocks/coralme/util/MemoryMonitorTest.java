package com.coralblocks.coralme.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MemoryMonitorTest {

    @Test
    void testMemoryMonitoring() throws InterruptedException {
        MemoryMonitor monitor = new MemoryMonitor();

        // Wait for the monitor to update at least once
        Thread.sleep(1100);

        long availableMemory = monitor.getAvailableMemory();

        // Verify that the reported available memory is within reasonable bounds
        assertTrue(availableMemory > 0);
        assertTrue(availableMemory <= Runtime.getRuntime().maxMemory());

        monitor.shutdown();
    }

    @Test
    void testMonitoringEfficiency() {
        MemoryMonitor monitor = new MemoryMonitor();

        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            monitor.getAvailableMemory();
        }
        long endTime = System.nanoTime();

        // Verify that getting the available memory is a low-latency operation
        long averageNanosPerCall = (endTime - startTime) / 1000000;
        assertTrue(averageNanosPerCall < 100); // Less than 100 nanoseconds per call

        monitor.shutdown();
    }
}
