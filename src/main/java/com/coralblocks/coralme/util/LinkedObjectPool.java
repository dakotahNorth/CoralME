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
 * An object pool backed up by an internal linked list. Note that instances will be created on
 * demand if the pool runs out of instances. This implementation includes adaptive memory management
 * to prevent excessive growth.
 *
 * <p><b>NOTE:</b> This data structure is designed on purpose to be used by <b>single-threaded
 * systems</b>, in other words, it will break if used concurrently by multiple threads.
 *
 * @param <E> the type of objects this object pool will hold
 */
public class LinkedObjectPool<E> implements ObjectPool<E> {

    private final LinkedObjectList<E> queue;
    private final Supplier<? extends E> supplier;
    private final MemoryMonitor memoryMonitor;
    private static final double MEMORY_THRESHOLD = 0.05; // 5% of max memory

    /**
     * Creates a LinkedObjectPool with adaptive memory management.
     *
     * @param initialSize the initial size of the pool (how many instance it will initially have)
     * @param s the supplier that will be used to create the instances
     */
    public LinkedObjectPool(int initialSize, Supplier<? extends E> s) {
        supplier = s;
        queue = new LinkedObjectList<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            queue.addLast(supplier.get());
        }
        memoryMonitor = new MemoryMonitor(1000); // Update every second
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
     * Get an instance from the pool or create a new one if the pool is empty.
     *
     * @return an instance from the pool
     */
    @Override
    public final E get() {
        if (queue.isEmpty()) {
            return supplier.get();
        }
        return queue.removeLast();
    }

    /**
     * Returns an instance back to the pool. The pool growth is controlled based on available
     * memory.
     *
     * @param e the instance to return back to the pool (i.e. release to the pool)
     */
    @Override
    public final void release(E e) {
        long availableMemory = memoryMonitor.getAvailableMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        if (availableMemory > maxMemory * MEMORY_THRESHOLD) {
            queue.addLast(e);
        }
        // If memory is low, the object is not added to the pool and will be garbage collected
    }
}
