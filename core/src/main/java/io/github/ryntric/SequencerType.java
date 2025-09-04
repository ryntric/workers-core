package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 10:44 PM
 * </p>
 * Types of sequencers used in a ring buffer to coordinate producers.
 * <p>
 * The sequencer determines how sequence numbers are claimed and published
 * in multi-threading environments.
 * </p>
 */

public enum SequencerType {
    /**
     * Single-producer sequencer.
     * <p>
     * Assumes only one producer thread will claim sequences.
     * Offers minimal synchronization overhead and maximum throughput.
     * </p>
     */
    SINGLE_PRODUCER,

    /**
     * Multi-producer sequencer.
     * <p>
     * Supports multiple producer threads claiming sequences concurrently.
     * Requires additional coordination and atomic operations to avoid
     * sequence conflicts.
     * </p>
     */
    MULTI_PRODUCER
}
