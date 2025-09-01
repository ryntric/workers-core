package io.github.ryntric.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * author: ryntric
 * date: 9/1/25
 * time: 8:44 AM
 **/

public final class UnsafeUtil {

    private UnsafeUtil() {}

    private static final Unsafe UNSAFE = ThrowableSupplier.sneaky(() -> {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    });

    public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    public static void setMemory(long address, long bytes, byte value) {
        UNSAFE.setMemory(address, bytes, value);
    }

    public static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    public static int getIntVolatile(long address) {
        return UNSAFE.getIntVolatile(null, address);
    }

    public static void putOrderedInt(long address, int value) {
        UNSAFE.putOrderedInt(null, address, value);
    }

    public static void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }
}
