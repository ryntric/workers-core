package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 11:08 PM
 * <p/>
 * Base implementation of a {@link Sequencer}, providing common functionality
 * for managing cursor and gating sequences in a ring buffer.
 * <p>
 * A sequencer coordinates producers and consumers by tracking:
 * <ul>
 *   <li><b>cursorSequence</b> — the last published sequence (producer's position)</li>
 *   <li><b>gatingSequence</b> — the last consumed sequence (consumer's position)</li>
 * </ul>
 * </p>
 **/

abstract class AbstractSequencer implements Sequencer {
    public static final long INITIAL_CURSOR_VALUE = -1L;

    protected final int bufferSize;
    protected final Sequence cursorSequence;
    protected final Sequence gatingSequence;
    protected final WaitPolicy waitPolicy;

    /**
     * Creates a new sequencer.
     *
     * @param waitPolicy  the waiting strategy for producers
     * @param bufferSize  the size of the ring buffer
     */
    public AbstractSequencer(WaitPolicy waitPolicy, int bufferSize) {
        this.bufferSize = bufferSize;
        this.waitPolicy = waitPolicy;
        this.cursorSequence = new Sequence(INITIAL_CURSOR_VALUE);
        this.gatingSequence = new Sequence(INITIAL_CURSOR_VALUE);
    }

    /**
     * Waits until the given wrap point is available by comparing it with the gating sequence.
     *
     * @param gatingSequence the consumer sequence to check against
     * @param wrapPoint      the sequence value that must not overrun consumers
     * @return the latest gating sequence once it is safe to proceed
     */
    protected final long await(Sequence gatingSequence, long wrapPoint) {
        long gating;
        do {
            gating = gatingSequence.getAcquire();
        } while (wrapPoint > gating);
        return gating;
    }

    /**
     * Validates that a claimed sequence value lies within the buffer bounds.
     *
     * @param value      the claimed value
     * @param bufferSize the buffer size
     * @throws IllegalArgumentException if the value is out of range [1, bufferSize]
     */
    protected final void checkConstraintOfClaimedValue(int value, int bufferSize) {
        if (((value - 1) | (bufferSize - value)) < 0) {
            throw new IllegalArgumentException("Claimed value " + value + " is invalid: must be between 1 and " + bufferSize);
        }
    }

    @Override
    public final Sequence getCursorSequence() {
        return cursorSequence;
    }

    @Override
    public final Sequence getGatingSequence() {
        return gatingSequence;
    }

    @Override
    public final int size() {
        return bufferSize;
    }

    @Override
    public final int distance() {
        return (int) (cursorSequence.getAcquire() - gatingSequence.getAcquire());
    }
}
