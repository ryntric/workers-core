package io.github.ryntric;

import io.github.ryntric.util.Util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * author: ryntric
 * date: 8/14/25
 * time: 10:56 AM
 **/

public final class ManyToOneSequencer extends AbstractSequencer {
    private static final VarHandle AVAILABLE_SLOT_BUFFER_VH = MethodHandles.arrayElementVarHandle(int[].class);

    private final int[] availableSlotBuffer;
    private final int indexShift;
    private final long mask;

    public ManyToOneSequencer(WaitPolicy waitPolicy, int bufferSize) {
        super(waitPolicy, bufferSize);
        this.availableSlotBuffer = new int[Constants.ARRAY_PADDING * 2 + bufferSize];
        this.mask = bufferSize - 1;
        this.indexShift = Util.log2(bufferSize);
        Arrays.fill(availableSlotBuffer, -1);
    }

    private int calculateAvailabilityFlag(long value) {
        return (int) (value >>> indexShift);
    }

    private boolean isAvailable(long value) {
        int index = Util.wrapPaddedIndex(value, mask);
        int flag = calculateAvailabilityFlag(value);
        return (int) AVAILABLE_SLOT_BUFFER_VH.getAcquire(availableSlotBuffer, index) == flag;
    }

    private void setAvailable(long value) {
        setAvailableBufferValue(Util.wrapPaddedIndex(value, mask), calculateAvailabilityFlag(value));
    }

    private void setAvailableBufferValue(int index, int flag) {
        AVAILABLE_SLOT_BUFFER_VH.setRelease(availableSlotBuffer, index, flag);
    }

    @Override
    public long next(int n) {
        long gating = gatingSequence.getPlain();

        long current = cursorSequence.getAndAddVolatile(n);
        long next = current + n;
        long wrapPoint = next - bufferSize;

        if (wrapPoint > gating) {
            await(gatingSequence, wrapPoint);
        }

        return next;
    }

    @Override
    public void publish(long value) {
        setAvailable(value);
    }

    @Override
    public void publish(long low, long high) {
        for (long i = low; i <= high; i++) {
            publish(i);
        }
    }

    @Override
    public long getHighestPublishedSequence(long next, long available) {
        for (long sequence = next; sequence <= available; sequence++) {
            if (!isAvailable(sequence)) {
                return sequence - 1;
            }
        }
        return available;
    }
}
