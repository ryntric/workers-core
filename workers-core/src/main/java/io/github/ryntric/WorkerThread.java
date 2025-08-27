package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/13/25
 * time: 12:00 AM
 **/

public final class WorkerThread<T> extends Thread {
    private final PaddedBoolean running = new PaddedBoolean();

    private final EventHandler<T> handler;
    private final EventPoller<T> poller;
    private final WaitPolicy waitPolicy;

    public WorkerThread(String name, ThreadGroup group, RingBuffer<T> buffer, WaitPolicy waitPolicy, EventHandler<T> handler, BatchSizeLimit limit) {
        super(group, name);
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
        handler.onStart();
        while (running.getAcquire()) {
            if (poller.poll(handler) == PollState.IDLE) {
                waitPolicy.await();
            }
        }
        handler.onShutdown();
    }

    public void shutdown() {
        running.setRelease(false);
    }


}
