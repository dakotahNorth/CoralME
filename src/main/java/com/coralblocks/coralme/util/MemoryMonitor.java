package com.coralblocks.coralme.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryMonitor {
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final AtomicLong availableMemory = new AtomicLong(0);
    private final Thread monitorThread;
    private static final long UPDATE_INTERVAL = 1000; // 1 second

    public MemoryMonitor() {
        monitorThread = new Thread(this::monitorMemory);
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    private void monitorMemory() {
        while (!Thread.currentThread().isInterrupted()) {
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            long newAvailableMemory = heapMemoryUsage.getMax() - heapMemoryUsage.getUsed();
            availableMemory.set(newAvailableMemory);
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public long getAvailableMemory() {
        return availableMemory.get();
    }

    public void shutdown() {
        monitorThread.interrupt();
    }
}
