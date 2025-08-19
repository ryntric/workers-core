package io.github.ryntric;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * author: ryntric
 * date: 8/12/25
 * time: 12:28 PM
 **/

final class PollerBlockingPolicy implements PollerWaitPolicy {
    private final AtomicBoolean isBlocked = new AtomicBoolean(false);
    private final Object MUTEX = new Object();

    @Override
    public long await(Sequence cursor, long sequence) throws InterruptedException {
        long available = 0L;
        if ((available = cursor.getAcquire()) < sequence) {
            isBlocked.setRelease(true);
            synchronized (MUTEX) {
                while ((available = cursor.getAcquire()) < sequence) {
                    MUTEX.wait();
                }
            }
        }
        return available;
    }

    @Override
    public void signal() {
        if (isBlocked.compareAndSet(true, false)) {
            synchronized (MUTEX) {
                MUTEX.notify();
            }
        }
    }
}
