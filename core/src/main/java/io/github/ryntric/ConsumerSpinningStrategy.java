package io.github.ryntric;


final class ConsumerSpinningStrategy implements ConsumerWaitStrategy {
    @Override
    public void await() {
        Thread.onSpinWait();
    }

    @Override
    public void signal() {
        // no-op
    }
}
