package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/9/25
 * time: 10:11 AM
 **/

public interface EventHandler<T> {
    void onEvent(T event, long sequence);

    void onError(T event, long sequence, Throwable ex);

    void onStart();

    void onShutdown();

}
