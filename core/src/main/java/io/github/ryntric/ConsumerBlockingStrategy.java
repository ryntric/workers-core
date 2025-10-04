package io.github.ryntric;


final class ConsumerBlockingStrategy implements ConsumerWaitStrategy {
    private final Object MUTEX = new Object();
    private boolean isBlocked = true;

    @Override
    public void await() {
        try {
            synchronized (MUTEX) {
                while (isBlocked) {
                    MUTEX.wait();
                }
                isBlocked = true;

            }
        } catch (InterruptedException ignored) {
            // no-op
        }

    }

    @Override
    public void signal() {
        synchronized (MUTEX) {
            isBlocked = false;
            MUTEX.notifyAll();
        }
    }
}
