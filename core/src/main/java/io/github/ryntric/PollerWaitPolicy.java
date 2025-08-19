package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/12/25
 * time: 11:07 AM
 **/

public interface PollerWaitPolicy {
    long await(Sequence cursor, long sequence) throws InterruptedException;

    void signal();
}
