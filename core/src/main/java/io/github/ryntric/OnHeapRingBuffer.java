package io.github.ryntric;

import io.github.ryntric.util.Util;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 10:38 PM
 **/

@SuppressWarnings("unchecked")
public final class OnHeapRingBuffer<T> extends AbstractRingBuffer<T> {
    private final T[] buffer;

    public OnHeapRingBuffer(EventFactory<T> factory, SequencerType sequencerType, WaitPolicy waitPolicy, int size) {
        super(size, sequencerType, waitPolicy);
        this.buffer = Util.fillEventBuffer(factory, (T[]) new Object[(Constants.OBJECT_ARRAY_PADDING << 1) + size]);
    }

    private int wrapIndex(long sequence, long mask) {
        return Util.wrapIndex(sequence, mask) + Constants.OBJECT_ARRAY_PADDING;
    }

    @Override
    public T get(long sequence) {
        return buffer[wrapIndex(sequence, mask)];
    }

}
