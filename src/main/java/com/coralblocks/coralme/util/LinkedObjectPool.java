/*
 * Copyright 2023 (c) CoralBlocks - http://www.coralblocks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralme.util;

import java.util.function.Supplier;

/**
 * An object pool backed up by an internal linked list with adaptive memory management. Instances
 * will be created on demand if the pool runs out of instances, and the pool's growth is limited
 * based on available memory.
 *
 * <p><b>NOTE:</b> This data structure is designed to be used by <b>single-threaded systems</b> for
 * object pooling, but includes a separate thread for memory monitoring.
 *
 * @param <E> the type of objects this object pool will hold
 */
public class LinkedObjectPool<E> implements ObjectPool<E> {

    private final LinkedObjectList<E> queue;
    private final Supplier<? extends E> supplier;
    private volatile long availableMemory;
    private final MemoryMonitor memoryMonitor;
    private static final double MEMORY_THRESHOLD = 0.05; // 5% of max memory

    /**
     * Creates a LinkedObjectPool with adaptive memory management.
     *
     * @param initialSize the initial size of the pool (how many instances it will initially have)
     * @param s the supplier that will be used to create the instances
     */
    public LinkedObjectPool(int initialSize, Supplier<? extends E> s) {
        supplier = s;
        queue = new LinkedObjectList<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            queue.addLast(supplier.get());
        }
        memoryMonitor = new MemoryMonitor(this);
        memoryMonitor.start();
    }

    /**
     * The number of instances currently inside this pool. Note that if all the instances are
     * checked-out from this pool, the size returned will be zero.
     *
     * @return the number of instances currently sitting inside this pool (and not checked-out by
     *     anyone)
     */
    public final int size() {
        return queue.size();
    }

    /**
     * Retrieves an instance from the pool or creates a new one if the pool is empty. This method
     * considers memory constraints when creating new instances.
     *
     * @return an instance from the pool
     */
    @Override
    public final E get() {
        if (queue.isEmpty()) {
            if (isMemoryAvailable()) {
                return supplier.get();
            } else {
                throw new OutOfMemoryError("Cannot create new instance due to memory constraints");
            }
        }
        return queue.removeLast();
    }

    /**
     * Returns an instance back to the pool. This method considers memory constraints when adding
     * the instance back to the pool.
     *
     * @param e the instance to return back to the pool (i.e. release to the pool)
     */
    @Override
    public final void release(E e) {
        if (isMemoryAvailable()) {
            queue.addLast(e);
        }
        // If memory is not available, the object is discarded and left for garbage collection
    }

    /**
     * Checks if there's enough memory available to add or create new objects.
     *
     * @return true if memory is available, false otherwise
     */
    private boolean isMemoryAvailable() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return availableMemory > maxMemory * MEMORY_THRESHOLD;
    }

    /**
     * Updates the available memory. This method is called by the MemoryMonitor.
     *
     * @param availableMemory the current available memory
     */
    void updateAvailableMemory(long availableMemory) {
        this.availableMemory = availableMemory;
    }

    /** Stops the memory monitor thread. Should be called when the pool is no longer needed. */
    public void shutdown() {
        memoryMonitor.stopMonitoring();
    }
}
