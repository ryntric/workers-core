package io.github.ryntric;


import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractPoller<T> implements Poller<T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractPoller.class.getName());

    private String createErrorMessage(T item, long sequence) {
        return "Error while processing item with sequence  " + sequence + ", item " + item;
    }

    protected final void handle(Consumer<T> consumer, T item, long sequence) {
        try {
            consumer.accept(item);
        } catch (Throwable ex) {
            LOGGER.log(Level.WARNING, createErrorMessage(item, sequence), ex);
        }
    }
}
