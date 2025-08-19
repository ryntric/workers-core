package io.github.ryntric;

import java.util.concurrent.locks.LockSupport;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 5:02 PM
 **/

final class PollerParkingPolicy implements PollerWaitPolicy {

    @Override
    public long await(Sequence cursor, long sequence) throws InterruptedException {
        long available = 0L;
        while ((available = cursor.getAcquire()) < sequence) {
            LockSupport.parkNanos(1L);
        }
        return available;
    }

    @Override
    public void signal() {

    }
}
