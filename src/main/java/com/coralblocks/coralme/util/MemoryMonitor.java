package com.coralblocks.coralme.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryMonitor {
    private final MemoryMXBean memoryMXBean;
    private volatile long availableMemory;
    private final Thread monitorThread;
    private final long updateInterval;

    public MemoryMonitor(long updateIntervalMs) {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.updateInterval = updateIntervalMs;
        this.monitorThread = new Thread(this::monitorMemory);
        this.monitorThread.setDaemon(true);
    }

    public void start() {
        monitorThread.start();
    }

    private void monitorMemory() {
        while (!Thread.currentThread().isInterrupted()) {
            updateAvailableMemory();
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateAvailableMemory() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        availableMemory = heapMemoryUsage.getMax() - heapMemoryUsage.getUsed();
    }

    public long getAvailableMemory() {
        return availableMemory;
    }
}
