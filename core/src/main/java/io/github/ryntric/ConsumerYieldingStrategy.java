package io.github.ryntric;


final class ConsumerYieldingStrategy implements ConsumerWaitStrategy {
    @Override
    public void await() {
        Thread.yield();
    }

    @Override
    public void signal() {
        //  no-op
    }
}
