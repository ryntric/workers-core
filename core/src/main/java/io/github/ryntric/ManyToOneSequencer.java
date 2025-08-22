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

abstract class ManyToOneSequencerLeftPaddings extends AbstractSequencer {
    protected byte
            p10, p11, p12, p13, p14, p15, p16, p17,
            p20, p21, p22, p23, p24, p25, p26, p27,
            p30, p31, p32, p33, p34, p35, p36, p37,
            p40, p41, p42, p43, p44, p45, p46, p47,
            p50, p51, p52, p53, p54, p55, p56, p57,
            p60, p61, p62, p63, p64, p65, p66, p67,
            p70, p71, p72, p73, p74, p75, p76, p77;

    public ManyToOneSequencerLeftPaddings(WaitPolicy waitPolicy, int bufferSize) {
        super(waitPolicy, bufferSize);
    }
}

abstract class ManyToOneSequencerFields extends ManyToOneSequencerLeftPaddings {
    protected long cached = Sequence.INITIAL_VALUE;

    public ManyToOneSequencerFields(WaitPolicy waitPolicy, int bufferSize) {
        super(waitPolicy, bufferSize);
    }
}

abstract class ManyToOneSequencerRightPaddings extends ManyToOneSequencerFields {
    protected byte
            p10, p11, p12, p13, p14, p15, p16, p17,
            p20, p21, p22, p23, p24, p25, p26, p27,
            p30, p31, p32, p33, p34, p35, p36, p37,
            p40, p41, p42, p43, p44, p45, p46, p47,
            p50, p51, p52, p53, p54, p55, p56, p57,
            p60, p61, p62, p63, p64, p65, p66, p67,
            p70, p71, p72, p73, p74, p75, p76, p77;

    public ManyToOneSequencerRightPaddings(WaitPolicy waitPolicy, int bufferSize) {
        super(waitPolicy, bufferSize);
    }
}

public final class ManyToOneSequencer extends ManyToOneSequencerRightPaddings {
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
        return availableSlotBuffer[index] == flag || ((int) AVAILABLE_SLOT_BUFFER_VH.getAcquire(availableSlotBuffer, index)) == flag;
    }

    private void setAvailable(long value) {
        setAvailableBufferValue(Util.wrapPaddedIndex(value, mask), calculateAvailabilityFlag(value));
    }

    private void setAvailableBufferValue(int index, int flag) {
        AVAILABLE_SLOT_BUFFER_VH.setRelease(availableSlotBuffer, index, flag);
    }

    @Override
    public long next(int n) {
        long cached = this.cached;
        long next = cursorSequence.getAndAddVolatile(n) + n;
        long wrapPoint = next - bufferSize;

        if (wrapPoint > cached) {
            this.cached = await(gatingSequence, wrapPoint);
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
            setAvailable(i);
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
