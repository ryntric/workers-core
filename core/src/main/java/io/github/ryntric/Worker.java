package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:21 PM
 **/

public final class Worker<E> {
    private final AbstractRingBuffer<E> ringBuffer;
    private final WorkerThread<E> workerThread;

    public Worker(String name, ThreadGroup group, WaitPolicy policy, EventHandler<E> handler, BatchSizeLimit limit, AbstractRingBuffer<E> ringBuffer) {
        this.ringBuffer = ringBuffer;
        this.workerThread = new WorkerThread<>(name, group, ringBuffer, policy, handler, limit);
    }

    public void start() {
        workerThread.start();
    }

    public void shutdown() {
        workerThread.shutdown();
    }

    public <A> void publishEvent(EventTranslator.EventTranslatorOneArg<E, A> translator, A arg) {
        ringBuffer.publishEvent(translator, arg);
    }

    public <A> void publishEvents(EventTranslator.EventTranslatorOneArg<E, A> translator, A[] args) {
        ringBuffer.publishEvents(translator, args);
    }

    public <A, B> void publishEvent(EventTranslator.EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1) {
        ringBuffer.publishEvent(translator, arg0, arg1);
    }

    public <A, B> void publishEvents(EventTranslator.EventTranslatorTwoArg<E, A, B> translator, A[] arg0, B[] arg1) {
        ringBuffer.publishEvents(translator, arg0, arg1);
    }

    public <A, B, C> void publishEvent(EventTranslator.EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2) {
        ringBuffer.publishEvent(translator, arg0, arg1, arg2);
    }

    public <A, B, C> void publishEvents(EventTranslator.EventTranslatorThreeArg<E, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        ringBuffer.publishEvents(translator, arg0, arg1, arg2);
    }

    public <A, B, C, D> void publishEvent(EventTranslator.EventTranslatorFourArg<E, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        ringBuffer.publishEvent(translator, arg0, arg1, arg2, arg3);
    }

    public <A, B, C, D> void publishEvents(EventTranslator.EventTranslatorFourArg<E, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        ringBuffer.publishEvents(translator, arg0, arg1, arg2, arg3);
    }
}
