package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/12/25
 * time: 1:25 PM
 * </p>
 * Defines translators used to initialize events before they are published
 * to a ring buffer. Each translator provides a functional contract for
 * writing data into a pre-allocated event instance.
 * <p>
 * Translators decouple event creation from publishing, avoiding object
 * allocation on the hot path. Instead, an event instance is reused and
 * populated with the required state before being handed off to consumers.
 * </p>
 **/

public interface EventTranslator {

    /**
     * Translates data into an event using a single argument.
     *
     * @param <T> the event type
     * @param <A> the first argument type
     */
    interface EventTranslatorOneArg<T, A> {
        /**
         * Populates the given event with the provided argument.
         *
         * @param event the event instance to translate into
         * @param arg   the argument used to populate the event
         */
        void translateTo(T event, A arg);
    }

    /**
     * Translates data into an event using two arguments.
     *
     * @param <T> the event type
     * @param <A> the first argument type
     * @param <B> the second argument type
     */
    interface EventTranslatorTwoArg<T, A, B> {
        /**
         * Populates the given event with the provided arguments.
         *
         * @param event the event instance to translate into
         * @param arg0  the first argument
         * @param arg1  the second argument
         */
        void translateTo(T event, A arg0, B arg1);
    }

    /**
     * Translates data into an event using three arguments.
     *
     * @param <T> the event type
     * @param <A> the first argument type
     * @param <B> the second argument type
     * @param <C> the third argument type
     */
    interface EventTranslatorThreeArg<T, A, B, C> {
        /**
         * Populates the given event with the provided arguments.
         *
         * @param event the event instance to translate into
         * @param arg0  the first argument
         * @param arg1  the second argument
         * @param arg2  the third argument
         */
        void translateTo(T event, A arg0, B arg1, C arg2);
    }

    /**
     * Translates data into an event using four arguments.
     *
     * @param <T> the event type
     * @param <A> the first argument type
     * @param <B> the second argument type
     * @param <C> the third argument type
     * @param <D> the fourth argument type
     */
    interface EventTranslatorFourArg<T, A, B, C, D> {
        /**
         * Populates the given event with the provided arguments.
         *
         * @param event the event instance to translate into
         * @param arg0  the first argument
         * @param arg1  the second argument
         * @param arg2  the third argument
         * @param arg3  the fourth argument
         */
        void translateTo(T event, A arg0, B arg1, C arg2, D arg3);
    }

    /**
     * Translates data into an event using five arguments.
     *
     * @param <T> the event type
     * @param <A> the first argument type
     * @param <B> the second argument type
     * @param <C> the third argument type
     * @param <D> the fourth argument type
     * @param <E> the fifth argument type
     */
    interface EventTranslatorFiveArg<T, A, B, C, D, E> {
        /**
         * Populates the given event with the provided arguments.
         *
         * @param event the event instance to translate into
         * @param arg0  the first argument
         * @param arg1  the second argument
         * @param arg2  the third argument
         * @param arg3  the fourth argument
         * @param arg4  the fifth argument
         */
        void translateTo(T event, A arg0, B arg1, C arg2, D arg3, E arg4);
    }
}
