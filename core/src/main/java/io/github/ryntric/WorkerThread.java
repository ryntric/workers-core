package io.github.ryntric;

/**
 * A dedicated thread that consumes events from a ring buffer using an {@link EventPoller}.
 * <p>
 * This thread repeatedly polls for new events and dispatches them to the provided {@link EventHandler}.
 * It uses a {@link PaddedBoolean} flag to track its running state, avoiding false sharing,
 * and a {@link WaitPolicy} to control how the thread waits when idle.
 *
 * @param <T> the type of events processed by this thread
 */

public final class WorkerThread<T> extends Thread {
    private final PaddedBoolean running = new PaddedBoolean();

    private final String name;
    private final EventHandler<T> handler;
    private final EventPoller<T> poller;
    private final WaitPolicy waitPolicy;

    /**
     * Creates a new worker thread.
     *
     * @param name       name of the thread
     * @param group      thread group for the thread
     * @param buffer     the ring buffer to consume events from
     * @param waitPolicy strategy for waiting when no events are available
     * @param handler    event handler invoked for each event
     * @param limit      batch size limit for polling events
     */
    public WorkerThread(String name, ThreadGroup group, AbstractRingBuffer<T> buffer, WaitPolicy waitPolicy, EventHandler<T> handler, BatchSizeLimit limit) {
        super(group, name);
        this.name = name;
        this.handler = handler;
        this.poller = new EventPoller<>(buffer, limit);
        this.waitPolicy = waitPolicy;

    }

    /**
     * Starts the worker thread only if it is not already running.
     * Uses a compare-and-set on the padded boolean for thread-safe start.
     */
    @Override
    public void start() {
        if (running.compareAndSetVolatile(false, true)) {
            super.start();
        }
    }

    /**
     * Main event loop of the worker thread.
     * <ul>
     *     <li>Calls {@link EventHandler#onStart} at the beginning.</li>
     *     <li>Polls events using the {@link EventPoller} and dispatches them to the handler.</li>
     *     <li>If no events are available, waits according to the {@link WaitPolicy}.</li>
     *     <li>Calls {@link EventHandler#onShutdown} before exiting.</li>
     * </ul>
     */
    @Override
    public void run() {
        handler.onStart(name);
        while (running.getAcquire()) {
            if (poller.poll(name, handler) == PollState.IDLE) {
                waitPolicy.await();
            }
        }
        handler.onShutdown(name);
    }

    /**
     * Signals the worker thread to stop.
     * Sets the running flag to false using a release fence for proper memory visibility.
     */
    public void shutdown() {
        running.setRelease(false);
    }


}
