package io.github.ryntric;


final class Coordinator {
    private final ProducerWaitStrategy producerWaitStrategy;
    private final ConsumerWaitStrategy consumerWaitStrategy;


    Coordinator(ProducerWaitStrategy producerWaitStrategy, ConsumerWaitStrategy consumerWaitStrategy) {
        this.producerWaitStrategy = producerWaitStrategy;
        this.consumerWaitStrategy = consumerWaitStrategy;
    }

    public void producerWait() {
        producerWaitStrategy.await();
    }

    public void consumerWait() {
        consumerWaitStrategy.await();
    }

    public void wakeupConsumer() {
        consumerWaitStrategy.signal();
    }
}
