package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/8/25
 * time: 10:38 PM
 **/

public interface Sequencer {

    default long next() {
        return next(1);
    }

    long next(int n);

    void publish(long sequence);

    void publish(long low, long high);

    long getHighestPublishedSequence(long next, long available);

    int distance();

    int size();

    Sequence getCursorSequence();

    Sequence getGatingSequence();

}
