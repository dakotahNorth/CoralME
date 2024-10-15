package com.coralblocks.coralme.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class ArrayObjectPoolStressTest {

    @Test
    void testHighDemandScenario() throws InterruptedException {
        ArrayObjectPool<byte[]> pool =
                new ArrayObjectPool<>(100, 1000, () -> new byte[1024 * 1024]); // 1MB objects
        int numThreads = 20;
        int operationsPerThread = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger maxObservedSize = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(
                    () -> {
                        try {
                            for (int j = 0; j < operationsPerThread; j++) {
                                byte[] obj = pool.get();
                                // Simulate some work
                                Thread.sleep(1);
                                pool.release(obj);
                                maxObservedSize.updateAndGet(
                                        current -> Math.max(current, pool.size()));
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

        System.out.println("Max observed pool size: " + maxObservedSize.get());

        // Verify that the pool size remained within reasonable bounds
        assertTrue(pool.size() <= 1100); // 100 main pool + 1000 max backup pool
        assertTrue(maxObservedSize.get() <= 1100);

        pool.shutdown();
    }
}
