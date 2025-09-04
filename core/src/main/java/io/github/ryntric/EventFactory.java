package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/12/25
 * time: 1:39 PM
 * Event factory creates an event that will hold some data
 **/

public interface EventFactory<E> {
    E newEvent();
}
