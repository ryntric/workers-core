package io.github.ryntric;


final class ProducerSpinningStrategy implements ProducerWaitStrategy {
    @Override
    public void await() {
        Thread.onSpinWait();
    }
}
