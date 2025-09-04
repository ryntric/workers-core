package io.github.ryntric;

import io.github.ryntric.util.Util;

/**
 * An on-heap ring buffer implementation that stores events in a padded array.
 * <p>
 * This ring buffer pre-allocates all events using the provided {@link EventFactory}.
 * Padding is applied on both ends of the array to prevent false sharing and improve cache locality.
 *
 * @param <T> the type of events stored in the ring buffer
 */

@SuppressWarnings("unchecked")
public final class OnHeapRingBuffer<T> extends AbstractRingBuffer<T> {
    private final T[] buffer;

    /**
     * Creates a new on-heap ring buffer.
     *
     * @param factory       factory to pre-create events
     * @param sequencerType type of sequencer (single or multi-producer)
     * @param waitPolicy    waiting strategy for producers
     * @param size          number of events in the ring buffer
     */
    public OnHeapRingBuffer(EventFactory<T> factory, SequencerType sequencerType, WaitPolicy waitPolicy, int size) {
        super(size, sequencerType, waitPolicy);
        this.buffer = Util.fillEventBuffer(factory, (T[]) new Object[(Constants.OBJECT_ARRAY_PADDING << 1) + size]);
    }

    /**
     * Computes the wrapped index into the padded buffer array for a given sequence.
     *
     * @param sequence the sequence number
     * @param mask     the mask used for wrapping (bufferSize - 1)
     * @return the array index including padding offset
     */
    private int wrapIndex(long sequence, long mask) {
        return Util.wrapIndex(sequence, mask) + Constants.OBJECT_ARRAY_PADDING;
    }

    /**
     * Retrieves the event at the given sequence.
     *
     * @param sequence the sequence number of the event
     * @return the event at the given sequence
     */
    @Override
    public T get(long sequence) {
        return buffer[wrapIndex(sequence, mask)];
    }

}
