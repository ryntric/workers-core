package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 5:30 PM
 * </p>
 * Factory for creating {@link Worker} instances with consistent naming, thread group assignment, and ring buffer initialization.
 * @param <E> the type of events processed by the workers
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

    /**
     * Creates a new worker factory.
     *
     * @param name         base name for worker threads and thread group
     * @param waitPolicy   waiting strategy for the worker threads
     * @param eventHandler handler invoked for each event
     * @param limit        batch size limit for event processing
     * @param factory      ring buffer factory to create buffers for workers
     */
    public WorkerFactory(String name, WaitPolicy waitPolicy, EventHandler<E> eventHandler, BatchSizeLimit limit, RingBufferFactory<E> factory) {
        this.name = name;
        this.group = new ThreadGroup(String.format(THREAD_GROUP_NAME_TEMPLATE, name));
        this.waitPolicy = waitPolicy;
        this.eventHandler = eventHandler;
        this.limit = limit;
        this.factory = factory;
    }

    /**
     * Creates a new {@link Worker} instance.
     * <p>
     * Each worker is assigned a unique name and its own ring buffer.
     * All workers share the same thread group and configuration.
     * </p>
     *
     * @return a new worker instance
     */
    public Worker<E> newWorker() {
        return new Worker<>(String.format(WORKER_NAME_TEMPLATE, name, counter++), group, waitPolicy, eventHandler, limit, factory.newRingBuffer());
    }

}
