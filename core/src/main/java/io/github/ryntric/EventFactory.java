package io.github.ryntric;

/**
 * Event factory creates an event that will hold some data
 **/

public interface EventFactory<E> {
    /**
     * Creates a new event
     * @return the event
     */
    E newEvent();
}
