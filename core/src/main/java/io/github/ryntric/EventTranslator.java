package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/12/25
 * time: 1:25 PM
 **/

public interface EventTranslator {
    interface EventTranslatorOneArg<T, A> {
        void translateTo(T event, A arg);
    }

    interface EventTranslatorTwoArg<T, A, B> {
        void translateTo(T event, A arg0, B arg1);
    }

    interface EventTranslatorThreeArg<T, A, B, C> {
        void translateTo(T event, A arg0, B arg1, C arg2);
    }

    interface EventTranslatorFourArg<T, A, B, C, D> {
        void translateTo(T event, A arg0, B arg1, C arg2, D arg3);
    }

    interface EventTranslatorFiveArg<T, A, B, C, D, E> {
        void translateTo(T event, A arg0, B arg1, C arg2, D arg3, E arg4);
    }
}
