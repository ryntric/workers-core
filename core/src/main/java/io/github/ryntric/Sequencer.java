package io.github.ryntric;


interface Sequencer {

    default long next(Coordinator coordinator) {
        return next(coordinator, 1);
    }

    long next(Coordinator coordinator, int n);

    void publishCursorSequence(long sequence);

    void publishCursorSequence(long low, long high);

    void publishGatingSequence(long sequence);

    void advanceGatingSequence(long sequence, long current);

    long getHighest(long low, long high);

    long getCursorSequenceAcquire();

    long getGatingSequencePlain();

    long wait(Coordinator coordinator, Sequence gatingSequence, long wrapPoint);

}
