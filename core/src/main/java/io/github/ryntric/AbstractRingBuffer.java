package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorFiveArg;
import io.github.ryntric.EventTranslator.EventTranslatorFourArg;
import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import io.github.ryntric.EventTranslator.EventTranslatorThreeArg;
import io.github.ryntric.EventTranslator.EventTranslatorTwoArg;
import io.github.ryntric.util.Util;

import static io.github.ryntric.SequencerType.SINGLE_PRODUCER;

/**
 * author: ryntric
 * date: 8/29/25
 * time: 1:58 PM
 **/

public abstract class AbstractRingBuffer<T> {
    protected final int size;
    protected final long mask;
    protected final Sequencer sequencer;

    protected AbstractRingBuffer(int size, SequencerType sequencerType, WaitPolicy waitPolicy) {
        this.size = Util.assertThatPowerOfTwo(size);
        this.mask = size - 1;
        this.sequencer = sequencerType == SINGLE_PRODUCER ? new OneToOneSequencer(waitPolicy, size) : new ManyToOneSequencer(waitPolicy, size);
    }

    public abstract T get(long sequence);

    public final int size() {
        return size;
    }

    public final int distance() {
        return sequencer.distance();
    }

    public final Sequencer getSequencer() {
        return sequencer;
    }

    public final  <A> void publishEvent(EventTranslatorOneArg<T, A> translator, A arg) {
        long next = sequencer.next();
        translateAndPublish(translator, arg, next);
    }

    private <A> void translateAndPublish(EventTranslatorOneArg<T, A> translator, A arg, long next) {
        try {
            translator.translateTo(get(next), arg);
        } finally {
            sequencer.publish(next);
        }
    }

    public final <A> void publishEvents(EventTranslatorOneArg<T, A> translator, A[] args) {
        int batchSize = args.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);

        translateAndPublish(translator, args, batchSize, low, high);
    }

    private <A> void translateAndPublish(EventTranslatorOneArg<T, A> translator, A[] args, int size, long low, long high) {
        long sequence = low;
        try {
            for (int i = 0; i < size; i++) {
                translator.translateTo(get(sequence++), args[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

    public final <A, B> void publishEvent(EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        long next = sequencer.next();
        translateAndPublish(translator, arg0, arg1, next);
    }

    private <A, B> void translateAndPublish(EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1, long next) {
        try {
            translator.translateTo(get(next), arg0, arg1);
        } finally {
            sequencer.publish(next);
        }
    }

    public final <A, B> void publishEvents(EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);

        translateAndPublish(translator, arg0, arg1, batchSize, low, high);
    }

    private <A, B> void translateAndPublish(EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1, int size, long low, long high) {
        long sequence = low;
        try {
            for (int i = 0; i < size; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

    public final <A, B, C> void publishEvent(EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        long next = sequencer.next();
        translateAndPublish(translator, arg0, arg1, arg2, next);
    }

    private <A, B, C> void translateAndPublish(EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2, long next) {
        try {
            translator.translateTo(get(next), arg0, arg1, arg2);
        } finally {
            sequencer.publish(next);
        }
    }

    public final <A, B, C> void publishEvents(EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);

        translateAndPublish(translator, arg0, arg1, arg2, batchSize, low, high);
    }

    private <A, B, C> void translateAndPublish(EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2, int size, long low, long high) {
        long sequence = low;
        try {
            for (int i = 0; i < size; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i], arg2[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

    public final <A, B, C, D> void publishEvent(EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        long next = sequencer.next();
        translateAndPublish(translator, arg0, arg1, arg2, arg3, next);
    }

    private <A, B, C, D> void translateAndPublish(EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3, long next) {
        try {
            translator.translateTo(get(next), arg0, arg1, arg2, arg3);
        } finally {
            sequencer.publish(next);
        }
    }

    public final <A, B, C, D> void publishEvents(EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);

        translateAndPublish(translator, arg0, arg1, arg2, arg3, batchSize, low, high);
    }

    private <A, B, C, D> void translateAndPublish(EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, int size, long low, long high) {
        long sequence = low;
        try {
            for (int i = 0; i < size; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i], arg2[i], arg3[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

    public final <A, B, C, D, E> void publishEvent(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        long next = sequencer.next();
        translateAndPublish(translator, arg0, arg1, arg2, arg3, arg4, next);
    }

    private <A, B, C, D, E> void translateAndPublish(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4, long next) {
        try {
            translator.translateTo(get(next), arg0, arg1, arg2, arg3, arg4);
        } finally {
            sequencer.publish(next);
        }
    }

    public final <A, B, C, D, E> void publishEvents(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3,  E [] arg4) {
        int batchSize = arg0.length;
        long high = sequencer.next(batchSize);
        long low = high - (batchSize - 1);

        translateAndPublish(translator, arg0, arg1, arg2, arg3, arg4, batchSize, low, high);
    }

    private <A, B, C, D, E> void translateAndPublish(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4, int size, long low, long high) {
        long sequence = low;
        try {
            for (int i = 0; i < size; i++) {
                translator.translateTo(get(sequence++), arg0[i], arg1[i], arg2[i], arg3[i], arg4[i]);
            }
        } finally {
            sequencer.publish(low, high);
        }
    }

}
