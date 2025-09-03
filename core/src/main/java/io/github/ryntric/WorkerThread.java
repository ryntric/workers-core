package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/13/25
 * time: 12:00 AM
 **/

public final class WorkerThread<T> extends Thread {
    private final PaddedBoolean running = new PaddedBoolean();

    private final String name;
    private final EventHandler<T> handler;
    private final EventPoller<T> poller;
    private final WaitPolicy waitPolicy;

    public WorkerThread(String name, ThreadGroup group, AbstractRingBuffer<T> buffer, WaitPolicy waitPolicy, EventHandler<T> handler, BatchSizeLimit limit) {
        super(group, name);
        this.name = name;
        this.handler = handler;
        this.poller = new EventPoller<>(buffer, limit);
        this.waitPolicy = waitPolicy;

    }

    @Override
    public void start() {
        if (running.compareAndSetVolatile(false, true)) {
            super.start();
        }
    }

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

    public void shutdown() {
        running.setRelease(false);
    }


}
