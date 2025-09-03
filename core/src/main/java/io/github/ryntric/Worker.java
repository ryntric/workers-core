package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorFiveArg;
import io.github.ryntric.EventTranslator.EventTranslatorFourArg;
import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import io.github.ryntric.EventTranslator.EventTranslatorThreeArg;
import io.github.ryntric.EventTranslator.EventTranslatorTwoArg;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:21 PM
 **/

public final class Worker<T> {
    private final AbstractRingBuffer<T> ringBuffer;
    private final WorkerThread<T> workerThread;

    public Worker(String name, ThreadGroup group, WaitPolicy policy, EventHandler<T> handler, BatchSizeLimit limit, AbstractRingBuffer<T> ringBuffer) {
        this.ringBuffer = ringBuffer;
        this.workerThread = new WorkerThread<>(name, group, ringBuffer, policy, handler, limit);
    }

    public void start() {
        workerThread.start();
    }

    public void shutdown() {
        workerThread.shutdown();
    }

    public <A> void publishEvent(EventTranslatorOneArg<T, A> translator, A arg) {
        ringBuffer.publishEvent(translator, arg);
    }

    public <A> void publishEvents(EventTranslatorOneArg<T, A> translator, A[] args) {
        ringBuffer.publishEvents(translator, args);
    }

    public <A, B> void publishEvent(EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        ringBuffer.publishEvent(translator, arg0, arg1);
    }

    public <A, B> void publishEvents(EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        ringBuffer.publishEvents(translator, arg0, arg1);
    }

    public <A, B, C> void publishEvent(EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        ringBuffer.publishEvent(translator, arg0, arg1, arg2);
    }

    public <A, B, C> void publishEvents(EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        ringBuffer.publishEvents(translator, arg0, arg1, arg2);
    }

    public <A, B, C, D> void publishEvent(EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        ringBuffer.publishEvent(translator, arg0, arg1, arg2, arg3);
    }

    public <A, B, C, D> void publishEvents(EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        ringBuffer.publishEvents(translator, arg0, arg1, arg2, arg3);
    }
    
    public <A, B, C, D, E> void publishEvent(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        ringBuffer.publishEvent(translator, arg0, arg1, arg2, arg3, arg4);
    }

    public <A, B, C, D, E> void publishEvents(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        ringBuffer.publishEvents(translator, arg0, arg1, arg2, arg3, arg4);
    }
}
