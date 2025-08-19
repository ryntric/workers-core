package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 4:43 PM
 **/

final class PollerYieldingPolicy implements PollerWaitPolicy {

    @Override
    public long await(Sequence cursor, long sequence) throws InterruptedException {
        long available = 0L;
        while ((available = cursor.getAcquire()) < sequence) {
            Thread.yield();
        }
        return available;
    }

    @Override
    public void signal() {

    }
}
