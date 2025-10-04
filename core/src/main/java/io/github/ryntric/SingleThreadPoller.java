package io.github.ryntric;


import java.util.function.Consumer;

final class SingleThreadPoller<T> implements Poller<T> {

    @Override
    public PollState poll(Sequencer sequencer, RingBuffer<T> ringBuffer, long batchSize, Consumer<T> consumer) {
        long current = sequencer.getGatingSequencePlain();
        long next = current + 1;
        long available = Long.min(sequencer.getCursorSequenceAcquire(), current + batchSize);

        if (next > available) {
            return PollState.IDLE;
        }

        long highest = sequencer.getHighest(next, available);
        for (; next <= highest; next++) {
            consumer.accept(ringBuffer.dequeue(next));
        }
        sequencer.publishGatingSequence(highest);
        return PollState.PROCESSING;
    }
}
