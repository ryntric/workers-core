package io.github.ryntric;


import java.util.concurrent.locks.LockSupport;

final class ProducerParkingStrategy implements ProducerWaitStrategy {

    @Override
    public void await() {
        LockSupport.parkNanos(1L);
    }
}
