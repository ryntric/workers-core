package io.github.ryntric;


import java.util.concurrent.locks.LockSupport;

final class ConsumerParkingStrategy implements ConsumerWaitStrategy {

    @Override
    public void await() {
        LockSupport.parkNanos(1L);
    }

    @Override
    public void signal() {
        // no-op
    }
}
