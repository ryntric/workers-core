package io.github.ryntric;

import io.github.ryntric.util.Util;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
final class RingBuffer<T> {
    private final T[] buffer;
    private final int size;
    private final int mask;
    private final Sequencer sequencer;
    private final Poller<T> poller;

    RingBuffer(Sequencer sequencer, Poller<T> poller, int size) {
        this.size = Util.assertThatPowerOfTwo(size);
        this.mask = size - 1;
        this.sequencer = sequencer;
        this.poller = poller;
        this.buffer = (T[]) new Object[(Constants.OBJECT_ARRAY_PADDING << 1) + size];
    }

    private int wrapIndex(long sequence, long mask) {
        return Util.wrapIndex(sequence, mask) + Constants.OBJECT_ARRAY_PADDING;
    }

    void drainTo(long low, long high, T[] dst) {
        int index;
        for (int i = 0; low <= high; ++low, ++i) {
            index = wrapIndex(low, mask);
            dst[i] = buffer[index];
            buffer[index] = null;
        }
    }

    T dequeue(long sequence) {
        int index = wrapIndex(sequence, mask);
        T value = buffer[index];
        buffer[index] = null;
        return value;
    }

    public PollerState poll(int batchsize, Consumer<T> consumer) {
        return poller.poll(sequencer, this, batchsize, consumer);
    }

    public void push(Coordinator coordinator, T item) {
        long sequence = sequencer.next(coordinator);
        buffer[wrapIndex(sequence, mask)] = item;
        sequencer.publishCursorSequence(sequence);
    }

    public void push(Coordinator coordinator, T[] items) {
        int length = items.length;
        long high = sequencer.next(coordinator, length);
        long low = high - (length - 1);

        for (int i = 0; i < items.length; i++) {
            buffer[wrapIndex(low + i, mask)] = items[i];
        }

        sequencer.publishCursorSequence(low, high);
    }

}
