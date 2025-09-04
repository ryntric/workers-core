package io.github.ryntric.util;

import io.github.ryntric.EventFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public final class Util {
    private Util() {}

    public static VarHandle findVarHandlePrivate(Class<?> clazz, String name, Class<?> type) {
        return ThrowableSupplier.sneaky(() -> MethodHandles.privateLookupIn(clazz, MethodHandles.lookup())
                .findVarHandle(clazz, name, type));
    }

    public static boolean isPowerOfTwo(int n) {
        return Integer.bitCount(n) == 1;
    }

    public static int assertThatPowerOfTwo(int n) {
        if (!isPowerOfTwo(n)) {
            throw new IllegalArgumentException("Should be power of two");
        }
        return n;
    }

    public static int assertBatchSizeGreaterThanZero(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than zero");
        }
        return batchSize;
    }

    public static int log2(int value) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(value) - 1;
    }

    public static <E> E[] fillEventBuffer(EventFactory<E> factory, E[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = factory.newEvent();
        }
        return buffer;
    }

    public static int wrapIndex(long sequence, long mask) {
        return (int) wrapLongIndex(sequence, mask);
    }

    public static long wrapLongIndex(long sequence, long mask) {
        return sequence & mask;
    }

    public static <A> void checkArgsLength(A[] args) {
        if (args.length >= 1) {}
    }

}
