package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:39 PM
 * </p>
 * Types of ring buffers that can be created.
 * <p>
 * Currently only {@link #ON_HEAP} is supported, representing a ring buffer
 * stored in the JVM heap. This enum can be extended in the future to
 * support off-heap or memory-mapped buffers.
 * </p>
 */

public enum RingBufferType {
    ON_HEAP,
}
