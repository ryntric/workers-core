package io.github.ryntric;


import java.util.function.Consumer;

public final class Channel<T> {
    private final Coordinator coordinator;
    private final RingBuffer<T> ringBuffer;

    private Channel(Coordinator coordinator, RingBuffer<T> ringBuffer) {
        this.coordinator = coordinator;
        this.ringBuffer = ringBuffer;
    }

    public void push(T item) {
        ringBuffer.push(coordinator, item);
        coordinator.wakeupConsumer();
    }

    public void push(T[] items) {
        ringBuffer.push(coordinator, items);
        coordinator.wakeupConsumer();
    }

    public void receive(int batchsize, Consumer<T> consumer) {
        ringBuffer.poll(batchsize, consumer);
    }

    public void blockingReceive(int batchsize, Consumer<T> consumer) {
        while (ringBuffer.poll(batchsize, consumer) == PollState.IDLE) {
            coordinator.consumerWait();
        }
    }

    private static ProducerWaitStrategy createProducerWaitStrategy(ProducerWaitStrategyType type) {
        ProducerWaitStrategy strategy = null;
        switch (type) {
            case PARKING:
                strategy = new ProducerParkingStrategy();
                break;
            case SPINNING:
                strategy = new ProducerSpinningStrategy();
                break;
            case YIELDING:
                strategy = new ProducerYieldingStrategy();
                break;
        }
        return strategy;
    }

    private static ConsumerWaitStrategy createConsumerWaitStrategy(ConsumerWaitStrategyType type) {
        ConsumerWaitStrategy strategy = null;
        switch (type) {
            case PARKING:
                strategy = new ConsumerParkingStrategy();
                break;
            case SPINNING:
                strategy = new ConsumerSpinningStrategy();
                break;
            case YIELDING:
                strategy = new ConsumerYieldingStrategy();
                break;
            case BLOCKING:
                strategy = new ConsumerBlockingStrategy();
                break;
        }
        return strategy;
    }

    public static <T> Channel<T> spsc(int capacity, ProducerWaitStrategyType pw, ConsumerWaitStrategyType cw) {
        Coordinator coordinator = new Coordinator(createProducerWaitStrategy(pw), createConsumerWaitStrategy(cw));
        Sequencer sequencer = new SingleProducerSequencer(capacity);
        Poller<T> poller = new SingleThreadPoller<>();
        return new Channel<>(coordinator, new RingBuffer<>(sequencer, poller, capacity));
    }

    public static <T> Channel<T> mpsc(int capacity, ProducerWaitStrategyType pw, ConsumerWaitStrategyType cw) {
        Coordinator coordinator = new Coordinator(createProducerWaitStrategy(pw), createConsumerWaitStrategy(cw));
        Sequencer sequencer = new MultiProducerSequencer(capacity);
        Poller<T> poller = new SingleThreadPoller<>();
        return new Channel<>(coordinator, new RingBuffer<>(sequencer, poller, capacity));
    }

    public static <T> Channel<T> spmc(int capacity, ProducerWaitStrategyType pw, ConsumerWaitStrategyType cw) {
        Coordinator coordinator = new Coordinator(createProducerWaitStrategy(pw), createConsumerWaitStrategy(cw));
        Sequencer sequencer = new SingleProducerSequencer(capacity);
        Poller<T> poller = new MultiThreadPoller<>();
        return new Channel<>(coordinator, new RingBuffer<>(sequencer, poller, capacity));
    }

    public static <T> Channel<T> mpmc(int capacity, ProducerWaitStrategyType pw, ConsumerWaitStrategyType cw) {
        Coordinator coordinator = new Coordinator(createProducerWaitStrategy(pw), createConsumerWaitStrategy(cw));
        Sequencer sequencer = new MultiProducerSequencer(capacity);
        Poller<T> poller = new MultiThreadPoller<>();
        return new Channel<>(coordinator, new RingBuffer<>(sequencer, poller, capacity));
    }

}
