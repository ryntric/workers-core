package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:30 PM
 **/

public final class WorkerFactory<E> {
    private static final String THREAD_GROUP_NAME_TEMPLATE = "%s-worker-tg";
    private static final String WORKER_NAME_TEMPLATE = "%s-worker-%d";

    private final String name;
    private final ThreadGroup group;
    private final WaitPolicy waitPolicy;
    private final EventHandler<E> eventHandler;
    private final BatchSizeLimit limit;
    private final RingBufferFactory<E> factory;

    private int counter = 0;

    public WorkerFactory(String name, WaitPolicy waitPolicy, EventHandler<E> eventHandler, BatchSizeLimit limit, RingBufferFactory<E> factory) {
        this.name = name;
        this.group = new ThreadGroup(String.format(THREAD_GROUP_NAME_TEMPLATE, name));
        this.waitPolicy = waitPolicy;
        this.eventHandler = eventHandler;
        this.limit = limit;
        this.factory = factory;
    }

    public Worker<E> newWorker() {
        return new Worker<>(String.format(WORKER_NAME_TEMPLATE, name, counter++), group, waitPolicy, eventHandler, limit, factory.newRingBuffer());
    }


}
