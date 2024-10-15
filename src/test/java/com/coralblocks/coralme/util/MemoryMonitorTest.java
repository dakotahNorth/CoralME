package com.coralblocks.coralme.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MemoryMonitorTest {

    @Test
    public void testMemoryMonitorReporting() throws InterruptedException {
        MemoryMonitor monitor = new MemoryMonitor();

        // Wait for the monitor to update at least once
        Thread.sleep(1100);

        long availableMemory = monitor.getAvailableMemory();
        assertTrue("Available memory should be positive", availableMemory > 0);
        assertTrue(
                "Available memory should be less than max memory",
                availableMemory <= Runtime.getRuntime().maxMemory());

        monitor.shutdown();
    }

    @Test
    public void testMemoryMonitorUpdates() throws InterruptedException {
        MemoryMonitor monitor = new MemoryMonitor();

        long initialMemory = monitor.getAvailableMemory();

        // Allocate some memory to change the available memory
        byte[] memoryConsumer = new byte[1024 * 1024 * 10]; // 10 MB

        // Wait for the monitor to update
        Thread.sleep(1100);

        long updatedMemory = monitor.getAvailableMemory();

        assertNotEquals("Memory should have changed", initialMemory, updatedMemory);
        assertTrue(
                "Updated memory should be less than initial memory", updatedMemory < initialMemory);

        monitor.shutdown();
    }
}
