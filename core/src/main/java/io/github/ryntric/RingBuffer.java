package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorFourArg;
import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import io.github.ryntric.EventTranslator.EventTranslatorThreeArg;
import io.github.ryntric.EventTranslator.EventTranslatorTwoArg;
import io.github.ryntric.util.Util;

import static io.github.ryntric.SequencerType.SINGLE_PRODUCER;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 10:38 PM
 **/

@SuppressWarnings("unchecked")
public final class RingBuffer<E> {
    private final int size;
    private final long mask;
    private final E[] buffer;
    private final Sequencer sequencer;

    public RingBuffer(EventFactory<E> factory, SequencerType type, PollerWaitPolicy pollerWaitPolicy, int size) {
        this.size = Util.assertThatPowerOfTwo(size);
        this.mask = size - 1;
        this.buffer = Util.fillEventBuffer(factory, (E[]) new Object[Constants.ARRAY_PADDING * 2 + size]);
        this.sequencer = type == SINGLE_PRODUCER ? new OneToOneSequencer(pollerWaitPolicy, size) : new ManyToOneSequencer(pollerWaitPolicy, size);
    }

    public int size() {
        return size;
    }

    public int distance() {
        return sequencer.distance();
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public E get(long sequence) {
        return buffer[Util.wrapPaddedIndex(sequence, mask)];
    }

    public <A> void publishEvent(EventTranslatorOneArg<E, A> translator, A arg) {
        long next = sequencer.next();
        translator.translateTo(get(next), arg);
        sequencer.publish(next);
    }

    public <A> void publishEvents(EventTranslatorOneArg<E, A> translator, A[] args) {
        int batchSize = args.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);
        long sequence = low;

        try {
            for (int i = 0; i < batchSize; i++) {
                translator.translateTo(get(sequence++), args[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }

    }

    public <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
        long next = sequencer.next();
        translator.translateTo(get(next), arg0, arg1);
        sequencer.publish(next);
    }

    public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);
        long sequence = low;

        try {
            for (int i = 0; i < batchSize; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

    public <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
        long next = sequencer.next();
        translator.translateTo(get(next), arg0, arg1, arg2);
        sequencer.publish(next);
    }

    public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);
        long sequence = low;

        try {
            for (int i = 0; i < batchSize; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i], arg2[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

    public <A, B, C, D> void publishEvent(EventTranslatorFourArg<E, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        long next = sequencer.next();
        translator.translateTo(get(next), arg0, arg1, arg2, arg3);
        sequencer.publish(next);
    }

    public <A, B, C, D> void publishEvents(EventTranslatorFourArg<E, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);
        long sequence = low;

        try {
            for (int i = 0; i < batchSize; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i], arg2[i], arg3[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

}
