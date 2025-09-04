package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:35 PM
 * </p>
 * Factory for creating {@link AbstractRingBuffer} instances with configurable
 * size, sequencing, and waiting strategies.
 *
 * <p>
 * This factory abstracts the creation of different ring buffer types
 * (currently only on-heap is implemented) and ensures proper initialization
 * with the chosen {@link EventFactory}, {@link WaitPolicy}, and {@link SequencerType}.
 * </p>
 *
 * @param <E> the type of events stored in the ring buffer
 **/

public final class RingBufferFactory<E> {
    private final int size;
    private final WaitPolicy waitPolicy;
    private final RingBufferType type;
    private final SequencerType sequencerType;
    private final EventFactory<E> eventFactory;

    /**
     * Creates a new ring buffer factory with full configuration.
     *
     * @param size          the ring buffer size (must be a power of 2)
     * @param waitPolicy    waiting strategy for producers
     * @param type          type of ring buffer
     * @param sequencerType type of sequencer
     * @param eventFactory  factory to create event instances
     */
    public RingBufferFactory(int size, WaitPolicy waitPolicy, RingBufferType type, SequencerType sequencerType, EventFactory<E> eventFactory) {
        this.size = size;
        this.waitPolicy = waitPolicy;
        this.type = type;
        this.sequencerType = sequencerType;
        this.eventFactory = eventFactory;
    }

    /**
     * Creates a new ring buffer factory with default ring buffer type {@link RingBufferType#ON_HEAP}.
     *
     * @param size          the ring buffer size (must be a power of 2)
     * @param waitPolicy    waiting strategy for producers
     * @param sequencerType type of sequencer
     * @param eventFactory  factory to create event instances
     */
    public RingBufferFactory(int size, WaitPolicy waitPolicy, SequencerType sequencerType, EventFactory<E> eventFactory) {
        this(size, waitPolicy, RingBufferType.ON_HEAP, sequencerType, eventFactory);
    }

    /**
     * Creates a new ring buffer instance with the configured parameters.
     *
     * @return a new {@link AbstractRingBuffer} instance
     */
    public AbstractRingBuffer<E> newRingBuffer() {
        return new OnHeapRingBuffer<>(eventFactory, sequencerType, waitPolicy, size);
    }
}
