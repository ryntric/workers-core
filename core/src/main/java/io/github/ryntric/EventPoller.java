package io.github.ryntric;

import io.github.ryntric.util.Util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * author: ryntric
 * date: 8/13/25
 * time: 12:00 AM
 **/

public final class EventPoller<T> extends Thread {
    private final AtomicBoolean running = new AtomicBoolean();

    private final EventHandler<T> handler;
    private final RingBuffer<T> buffer;
    private final Sequencer sequencer;
    private final Sequence sequence;
    private final Sequence gatingSequence;
    private final WaitPolicy waitPolicy;
    private final long batchSize;

    public EventPoller(String name, ThreadGroup group, RingBuffer<T> buffer, WaitPolicy waitPolicy, EventHandler<T> handler, BatchSizeLimit limit) {
        super(group, name);
        this.handler = handler;
        this.buffer = buffer;
        this.sequencer = buffer.getSequencer();
        this.sequence = sequencer.getGatingSequence();
        this.gatingSequence = sequencer.getCursorSequence();
        this.waitPolicy = waitPolicy;
        this.batchSize = Util.assertBatchSizeGreaterThanZero(limit.get(buffer.size()));
    }

    private long getHighest(long current, long next, long available) {
        return Long.min(current + batchSize, sequencer.getHighestPublishedSequence(next, available));
    }

    private long await(Sequence cursor, long next) {
        long available;
        while ((available = cursor.getAcquire()) < next) {
            waitPolicy.await();
        }
        return available;
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            super.start();
            handler.onStart();
        }
    }

    @Override
    public void run() {
        while (running.getAcquire()) {
            try {
                long current = sequence.getPlain();
                long next = current + 1;
                long available = await(gatingSequence, next);
                long highest = getHighest(current, next, available);

                while (next <= highest) {
                    T event = buffer.get(next);
                    handler.onEvent(event, next);
                    next++;
                }

                sequence.setRelease(next - 1);
            } catch (Throwable ex) {
                handler.onError(ex);
            }
        }
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            handler.onShutdown();
        }
    }


}
