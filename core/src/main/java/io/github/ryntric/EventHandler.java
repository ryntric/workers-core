package io.github.ryntric;

/**
 * A generic event handler interface that defines callbacks for processing events
 * and managing worker lifecycle states.
 **/

public interface EventHandler<T> {

    /**
     * @param name the worker thread name that processes the event
     * @param event the event to process
     * @param sequence the sequence number of the current event
     */
    void onEvent(String name, T event, long sequence);

    /**
     *
     * @param name the worker thread name that encountered the error
     * @param event the event that caused the error
     * @param sequence the sequence number of the current event
     * @param ex the exception that was thrown
     */
    void onError(String name, T event, long sequence, Throwable ex);

    /**
     * @param name the name of the worker
     */
    void onStart(String name);

    /**
     * @param name the name of the worker
     */
    void onShutdown(String name);

}
