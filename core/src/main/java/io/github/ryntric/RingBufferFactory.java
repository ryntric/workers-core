package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:35 PM
 **/

public final class RingBufferFactory<E> {
    private final int size;
    private final WaitPolicy waitPolicy;
    private final RingBufferType type;
    private final SequencerType sequencerType;
    private final EventFactory<E> eventFactory;

    public RingBufferFactory(int size, WaitPolicy waitPolicy, RingBufferType type, SequencerType sequencerType, EventFactory<E> eventFactory) {
        this.size = size;
        this.waitPolicy = waitPolicy;
        this.type = type;
        this.sequencerType = sequencerType;
        this.eventFactory = eventFactory;
    }

    public RingBufferFactory(int size, WaitPolicy waitPolicy, SequencerType sequencerType, EventFactory<E> eventFactory) {
        this(size, waitPolicy, RingBufferType.ON_HEAP, sequencerType, eventFactory);
    }

    public AbstractRingBuffer<E> newRingBuffer() {
        return new OnHeapRingBuffer<>(eventFactory, sequencerType, waitPolicy, size);
    }
}
