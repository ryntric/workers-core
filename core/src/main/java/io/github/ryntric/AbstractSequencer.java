package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 11:08 PM
 **/

abstract class AbstractSequencer implements Sequencer {
    public static final long INITIAL_CURSOR_VALUE = -1L;

    protected final int bufferSize;
    protected final Sequence cursorSequence;
    protected final Sequence gatingSequence;
    protected final WaitPolicy waitPolicy;

    public AbstractSequencer(WaitPolicy waitPolicy, int bufferSize) {
        this.bufferSize = bufferSize;
        this.waitPolicy = waitPolicy;
        this.cursorSequence = new Sequence(INITIAL_CURSOR_VALUE);
        this.gatingSequence = new Sequence(INITIAL_CURSOR_VALUE);
    }

    protected final long await(Sequence gatingSequence, long wrapPoint) {
        long gating = 0L;
        while (wrapPoint > (gating = gatingSequence.getAcquire())) {
            waitPolicy.await();
        }
        return gating;
    }

    @Override
    public Sequence getCursorSequence() {
        return cursorSequence;
    }

    @Override
    public Sequence getGatingSequence() {
        return gatingSequence;
    }

    @Override
    public int size() {
        return bufferSize;
    }

    @Override
    public int distance() {
        return (int) (cursorSequence.getAcquire() - gatingSequence.getAcquire());
    }
}
