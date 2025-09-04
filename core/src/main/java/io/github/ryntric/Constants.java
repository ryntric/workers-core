package io.github.ryntric;

import sun.misc.Unsafe;

/**
 * Low-level constants used for memory alignment and padding to reduce
 * false sharing and improve cache performance.
 * <p>
 * These are primarily used in ring buffers, off-heap structures, and
 * other high-performance concurrency primitives.
  **/

public interface Constants {
    /**
     * The size of a CPU cache line in bytes.
     * Typically 64 bytes on modern x86/x64 CPUs.
     */
    int CACHE_LINE_SIZE = 64;

    /**
     * Padding applied to object arrays to avoid false sharing between elements.
     * Calculated as the number of object references that fit in a cache line.
     */
    int OBJECT_ARRAY_PADDING = CACHE_LINE_SIZE / Unsafe.ARRAY_OBJECT_INDEX_SCALE;

    /**
     * Padding applied to off-heap byte buffers to align memory
     * to cache lines and reduce false sharing.
     */
    int BYTE_BUFFER_PADDING =  64;
}
