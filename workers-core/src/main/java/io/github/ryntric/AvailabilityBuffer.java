package io.github.ryntric;

import io.github.ryntric.util.Util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * author: vbondarchuk
 * date: 8/30/25
 * time: 8:51 AM
 **/

public final class AvailabilityBuffer {
    private static final VarHandle BUFFER_VH = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.nativeOrder());

    private final ByteBuffer buffer;
    private final long shift;
    private final long mask;

    public AvailabilityBuffer(int size) {
        this.buffer = ByteBuffer.allocateDirect(getCapacity(size))
                .order(ByteOrder.nativeOrder());
        this.mask = size - 1;
        this.shift = Util.log2(size);
        init(size);
    }

    private void init(int size) {
        for (int i = 0; i < size; i++) {
            buffer.putInt(i, (int) Sequence.INITIAL_VALUE);
        }
    }

    private int getCapacity(int size) {
        long capacity = (long) size << 2;
        if (capacity > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Requested capacity is too large" + capacity);
        }
        return (int) capacity;
    }

    private int calculateAvailabilityFlag(long sequence) {
        return (int) (sequence >>> shift);
    }

    private int calculateIndex(long sequence) {
        return Util.wrappedIndex(sequence, mask) << 2;
    }

    public boolean isAvailable(long sequence) {
        int index = calculateIndex(sequence);
        int flag = calculateAvailabilityFlag(sequence);
        return (int) BUFFER_VH.get(buffer, index) == flag || (int) BUFFER_VH.getAcquire(buffer, index) == flag;
    }

    public void set(long sequence) {
        int index = calculateIndex(sequence);
        int flag = calculateAvailabilityFlag(sequence);
        BUFFER_VH.setRelease(buffer, index, flag);
    }
}
