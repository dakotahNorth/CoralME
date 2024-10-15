package com.coralblocks.coralme.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ArrayObjectPoolTest {

    @Test
    void testAdaptiveGrowth() {
        ArrayObjectPool<String> pool = new ArrayObjectPool<>(5, 10, () -> "test");

        // Fill the main pool
        for (int i = 0; i < 5; i++) {
            pool.release("item" + i);
        }

        // Add more items to trigger backup pool usage
        for (int i = 5; i < 15; i++) {
            pool.release("item" + i);
        }

        // Verify that the total size is less than or equal to 15 (5 main + 10 backup)
        assertTrue(pool.size() <= 15);
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        ArrayObjectPool<String> pool = new ArrayObjectPool<>(5, 10, () -> "test");
        int numThreads = 10;
        int operationsPerThread = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(
                    () -> {
                        try {
                            for (int j = 0; j < operationsPerThread; j++) {
                                String obj = pool.get();
                                // Simulate some work
                                Thread.sleep(1);
                                pool.release(obj);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            latch.countDown();
                        }
                    });
        }

        latch.await();
        executorService.shutdown();

        // Verify that the pool size is within expected bounds
        assertTrue(pool.size() >= 5 && pool.size() <= 15);
    }
}
