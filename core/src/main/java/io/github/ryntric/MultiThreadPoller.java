package io.github.ryntric;


import java.util.function.Consumer;

final class MultiThreadPoller<T> implements Poller<T> {
    private final Sequence sequence = new Sequence(Sequence.INITIAL_VALUE);

    @Override
    public PollState poll(Sequencer sequencer, RingBuffer<T> ringBuffer, long batchSize, Consumer<T> consumer) {
        long current;
        long next;
        long available;
        long highest;

        do {
            current = sequence.getAcquire();
            next = current + 1;
            available = Long.min(sequencer.getCursorSequenceAcquire(), current + batchSize);

            if (next > available) {
                return PollState.IDLE;
            }

            highest = sequencer.getHighest(next, available);
        } while (!sequence.weakCompareAndSetVolatile(current, highest));

        for (; next <= highest; next++) {
            consumer.accept(ringBuffer.dequeue(next));
        }

        sequencer.advanceGatingSequence(highest, current);
        return PollState.PROCESSING;
    }

}
