package io.github.ryntric;

import java.util.concurrent.locks.LockSupport;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 5:49 PM
 * </p>
 * Defines the waiting strategy for consumers and producers in a {@link Sequencer} and {@link WorkerThread}.
 * <p>
 * Wait policies control how a thread waits for events to become available.
 * Different strategies offer trade-offs between CPU utilization, latency, and throughput.
 * </p>
 **/

public enum WaitPolicy {

    /**
     * Parking strategy.
     * <p>
     * The thread parks for a short period (nanoseconds) using {@link LockSupport#parkNanos(long)}.
     * Reduces CPU usage but may increase latency slightly.
     * Suitable for low-to-medium throughput systems where minimizing CPU spinning is desired.
     * </p>
     */
    PARKING {
        @Override
        protected void await() {
            LockSupport.parkNanos(1L);
        }
    },

    /**
     * Spinning strategy.
     * <p>
     * The thread actively spins using {@link Thread#onSpinWait()}.
     * Very low latency but consumes CPU while waiting.
     * Suitable for high-performance, low-latency systems where threads are expected
     * to wait only briefly.
     * </p>
     */
    SPINNING {
        @Override
        protected void await() {
            Thread.onSpinWait();
        }
    },

    /**
     * Yielding strategy.
     * <p>
     * The thread yields using {@link Thread#yield()}.
     * Frees up the CPU for other threads, trading some latency for better CPU sharing.
     * Useful when multiple threads are competing for CPU and moderate throughput is acceptable.
     * </p>
     */
    YIELDING {
        @Override
        protected void await() {
            Thread.yield();
        }
    };

    protected abstract void await();


}
