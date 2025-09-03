package io.github.ryntric;

import io.github.ryntric.util.UnsafeUtil;
import io.github.ryntric.util.Util;
import sun.misc.Unsafe;

/**
 * author: ryntric
 * date: 8/30/25
 * time: 8:51 AM
 **/

public final class AvailabilityBuffer {
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    private static final int SCALE_FACTOR = 2;

    private final long capacity;
    private final long baseAddress;
    private final long shift;
    private final long mask;

    public AvailabilityBuffer(int size) {
        this.capacity = getCapacity(size);
        this.baseAddress = UNSAFE.allocateMemory(capacity);
        this.mask = size - 1;
        this.shift = Util.log2(size);
        this.init();
    }

    private void init() {
        UNSAFE.setMemory(baseAddress, capacity, (byte) -1);
    }

    private long getCapacity(long size) {
        return (size << SCALE_FACTOR) + (Constants.BYTE_BUFFER_PADDING << 1);
    }

    private int calculateAvailabilityFlag(long sequence) {
        return (int) (sequence >>> shift);
    }

    private long calculateAddress(long sequence) {
        return (Util.wrapLongIndex(sequence, mask) << SCALE_FACTOR) + baseAddress + Constants.BYTE_BUFFER_PADDING;
    }

    public boolean isAvailable(long sequence) {
        long address = calculateAddress(sequence);
        int flag = calculateAvailabilityFlag(sequence);
        return UNSAFE.getInt(address) == flag || UNSAFE.getIntVolatile(null, address) == flag;
    }

    public void set(long sequence) {
        long address = calculateAddress(sequence);
        int flag = calculateAvailabilityFlag(sequence);
        UNSAFE.putOrderedInt(null, address, flag);
    }
}
