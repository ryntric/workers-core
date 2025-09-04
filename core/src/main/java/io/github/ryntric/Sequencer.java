package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 10:38 PM
 * </p>
 * Defines a sequencing strategy for a ring buffer.
 * <p>
 * The {@code Sequencer} is responsible for assigning sequence numbers to
 * producers, tracking the highest published sequence, and providing
 * visibility guarantees to consumers.
 * </p>
 **/

public interface Sequencer {

    /**
     * Claim the next sequence number for publishing a single event.
     *
     * @return the claimed sequence
     */
    default long next() {
        return next(1);
    }

    /**
     * Claim the next {@code n} sequence numbers for publishing multiple events (batching).
     *
     * @param n number of sequences to claim
     * @return the highest claimed sequence
     */
    long next(int n);

    /**
     * Publish a single sequence, making it visible to consumers.
     *
     * @param sequence the sequence to publish
     */
    void publish(long sequence);

    /**
     * Publish a range of sequences, making them visible to consumers.
     *
     * @param low  the lowest sequence in the batch
     * @param high the highest sequence in the batch
     */
    void publish(long low, long high);

    /**
     * Returns the highest sequence that has been published and is available
     * for consumers between {@code next} and {@code available}.
     *
     * @param next      the next sequence the producer intends to publish
     * @param available the highest sequence known to be available
     * @return the highest published sequence in the given range
     */
    long getHighestPublishedSequence(long next, long available);

    /**
     * Returns the number of events that are currently in the buffer
     * between the producer cursor and the gating sequence.
     *
     * @return the distance between cursor and gating sequence
     */
    int distance();

    /**
     * Returns the size of the ring buffer managed by this sequencer.
     *
     * @return the buffer size
     */
    int size();

    /**
     * Returns the sequence representing the producer cursor.
     * <p>
     * This sequence indicates the highest sequence claimed by a producer.
     * </p>
     *
     * @return the cursor sequence
     */
    Sequence getCursorSequence();

    /**
     * Returns the gating sequence that producers use to prevent
     * overwriting unprocessed entries.
     *
     * @return the gating sequence
     */
    Sequence getGatingSequence();

}
