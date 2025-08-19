package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 7:05 PM
 **/

final class PollerLoopingPolicy implements PollerWaitPolicy {

    @Override
    public long await(Sequence cursor, long sequence) throws InterruptedException {
        long available = 0L;
        while ((available = cursor.getAcquire()) < sequence) {
            // empty body
        }
        return available;
    }

    @Override
    public void signal() {

    }
}
