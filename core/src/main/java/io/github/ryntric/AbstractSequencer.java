package io.github.ryntric;

abstract class AbstractSequencer implements Sequencer {
    public static final long INITIAL_CURSOR_VALUE = -1L;

    protected final int bufferSize;
    protected final Sequence cursorSequence;
    protected final Sequence gatingSequence;

    public AbstractSequencer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.cursorSequence = new Sequence(INITIAL_CURSOR_VALUE);
        this.gatingSequence = new Sequence(INITIAL_CURSOR_VALUE);
    }

    @Override
    public final void publishGatingSequence(long sequence) {
        gatingSequence.setRelease(sequence);
    }

    @Override
    public final void advanceGatingSequence(long sequence, long current) {
        Sequence gatingSequence = this.gatingSequence;

        while (current < sequence && !gatingSequence.weakCompareAndSetVolatile(current, sequence)) {
            current = gatingSequence.getAcquire();
        }
    }

    @Override
    public final long getCursorSequenceAcquire() {
        return cursorSequence.getAcquire();
    }

    @Override
    public final long getGatingSequencePlain() {
        return gatingSequence.getPlain();
    }

    @Override
    public final long wait(Coordinator coordinator, Sequence gatingSequence, long wrapPoint) {
        long gating;
        while (wrapPoint > (gating = gatingSequence.getAcquire())) {
            coordinator.producerWait();
        }
        return gating;
    }

}
