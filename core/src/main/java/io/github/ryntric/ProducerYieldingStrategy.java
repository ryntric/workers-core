package io.github.ryntric;


final class ProducerYieldingStrategy implements ProducerWaitStrategy {
    @Override
    public void await() {
        Thread.yield();
    }
}
