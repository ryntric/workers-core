package io.github.ryntric;

import io.github.ryntric.util.Util;

/**
 * A poller that consumes events from an {@link AbstractRingBuffer} in batches, delegating processing to an {@link EventHandler}.
 * <p>
 * The poller advances a {@link Sequence} as it successfully processes events,
 * ensuring ordered consumption and backpressure control through the sequencer.
  *
 * <p>
 * The batch size determines the maximum number of events processed in a single
 * {@link #poll(String, EventHandler)} invocation.
  *
 * @param <T> the type of events stored in the buffer
 */

public final class EventPoller<T> {
    private final AbstractRingBuffer<T> buffer;
    private final Sequencer sequencer;
    private final Sequence sequence;
    private final Sequence gatingSequence;
    private final int batchSize;

    /**
     * Creates a new poller given buffer.
     * @param buffer the ring buffer from which will be polled events;
     * @param batchSizeLimit the maximum batch size limit as an integer of the buffer size
     * @throws IllegalArgumentException if the batch size is either greater or equal than 0
     */
    public EventPoller(AbstractRingBuffer<T> buffer, BatchSizeLimit batchSizeLimit) {
        this.buffer = buffer;
        this.sequencer = buffer.getSequencer();
        this.sequence = sequencer.getGatingSequence();
        this.gatingSequence = sequencer.getCursorSequence();
        this.batchSize = Util.assertBatchSizeGreaterThanZero(batchSizeLimit.get(buffer.size()));
    }

    /**
     * Invokes the handler for a single event, capturing and reporting errors.
     *
     * @param name     the worker thread name
     * @param handler  the event handler
     * @param event    the event to process
     * @param sequence the sequence number of the event
     */
    private void handle(String name, EventHandler<T> handler, T event, long sequence) {
        try {
            handler.onEvent(name, event, sequence);
        } catch (Throwable ex) {
            handler.onError(name, event, sequence, ex);
        }
    }

    /**
     * Polls the buffer for available events and processes them in batch,
     * up to the configured {@code batchSize}.
     *
     * @param name    the worker thread name
     * @param handler the event handler that will process events
     * @return the poll state:
     *         <ul>
     *             <li>{@link PollState#IDLE} if no events were available</li>
     *             <li>{@link PollState#PROCESSING} if one or more events were processed</li>
     *         </ul>
     */
    public PollState poll(String name, EventHandler<T> handler) {
        long current = sequence.getPlain();
        long next = current + 1;
        long available;

        if ((available = gatingSequence.getPlain()) < next && (available = gatingSequence.getAcquire()) < next) {
            return PollState.IDLE;
        }

        long highest = Long.min(current + batchSize, sequencer.getHighestPublishedSequence(next, available));
        for(; next <= highest; ++next) {
            handle(name, handler, buffer.get(next), next);
        }

        sequence.setRelease(next - 1);
        return PollState.PROCESSING;
    }

}
