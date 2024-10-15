package com.coralblocks.coralme.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryMonitor {
    private final AtomicLong availableMemory;
    private final MemoryMXBean memoryMXBean;
    private final Thread monitorThread;
    private volatile boolean running;

    public MemoryMonitor() {
        this.availableMemory = new AtomicLong(0);
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.running = true;

        this.monitorThread = new Thread(this::monitorMemory);
        this.monitorThread.setDaemon(true);
        this.monitorThread.start();
    }

    private void monitorMemory() {
        while (running && !Thread.currentThread().isInterrupted()) {
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            long newAvailableMemory = heapMemoryUsage.getMax() - heapMemoryUsage.getUsed();
            availableMemory.set(newAvailableMemory);

            try {
                Thread.sleep(1000); // Update every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public long getAvailableMemory() {
        return availableMemory.get();
    }

    public void shutdown() {
        running = false;
        monitorThread.interrupt();
    }
}
