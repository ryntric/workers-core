package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 5:01 PM
 **/

final class PollerSpinningPolicy implements PollerWaitPolicy {

    @Override
    public long await(Sequence cursor, long sequence) throws InterruptedException {
        long available = 0L;
        while ((available = cursor.getAcquire()) < sequence) {
            Thread.onSpinWait();
        }
        return available;
    }

    @Override
    public void signal() {

    }
}
