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
    protected final PollerWaitPolicy pollerWaitPolicy;

    public AbstractSequencer(PollerWaitPolicy pollerWaitPolicy, int bufferSize) {
        this.bufferSize = bufferSize;
        this.pollerWaitPolicy = pollerWaitPolicy;
        this.cursorSequence = new Sequence(INITIAL_CURSOR_VALUE);
        this.gatingSequence = new Sequence(INITIAL_CURSOR_VALUE);
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
