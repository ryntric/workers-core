package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/12/25
 * time: 1:25 PM
 **/

public interface EventTranslator {
    interface EventTranslatorOneArg<E, A> {
        void translateTo(E event, A arg);
    }

    interface EventTranslatorTwoArg<E, A, B> {
        void translateTo(E event, A arg0, B arg1);
    }

    interface EventTranslatorThreeArg<E, A, B, C> {
        void translateTo(E event, A arg0, B arg1, C arg2);
    }

    interface EventTranslatorFourArg<E, A, B, C, D> {
        void translateTo(E event, A arg0, B arg1, C arg2, D arg3);
    }
}
