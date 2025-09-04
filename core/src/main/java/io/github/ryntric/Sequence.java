package io.github.ryntric;

import io.github.ryntric.util.Util;

import java.lang.invoke.VarHandle;

abstract class LeftPaddings {
    protected byte
        p10, p11, p12, p13, p14, p15, p16, p17,
        p20, p21, p22, p23, p24, p25, p26, p27,
        p30, p31, p32, p33, p34, p35, p36, p37,
        p40, p41, p42, p43, p44, p45, p46, p47,
        p50, p51, p52, p53, p54, p55, p56, p57,
        p60, p61, p62, p63, p64, p65, p66, p67,
        p70, p71, p72, p73, p74, p75, p76, p77;
}

abstract class PaddedValue extends LeftPaddings {
    protected long value;

    public PaddedValue(long value) {
        this.value = value;
    }
}

abstract class RightPaddings extends PaddedValue {
    protected byte
            p10, p11, p12, p13, p14, p15, p16, p17,
            p20, p21, p22, p23, p24, p25, p26, p27,
            p30, p31, p32, p33, p34, p35, p36, p37,
            p40, p41, p42, p43, p44, p45, p46, p47,
            p50, p51, p52, p53, p54, p55, p56, p57,
            p60, p61, p62, p63, p64, p65, p66, p67,
            p70, p71, p72, p73, p74, p75, p76, p77;


    public RightPaddings(long value) {
        super(value);
    }
}

/**
 * A padded sequence counter used in ring buffers to track producer or consumer positions.
 * <p>
 * The {@code Sequence} value is padded on both sides to prevent false sharing
 * between neighboring fields in memory. False sharing occurs when multiple threads
 * modify adjacent fields that share the same cache line, leading to performance degradation.
 * </p>
 *
 * <p>
 * Access to {@code value} supports different memory semantics:
 * <ul>
 *   <li>{@link #getPlain()} / {@link #setPlain(long)} — plain read/write without memory fences</li>
 *   <li>{@link #getAcquire()} — acquire fence before read</li>
 *   <li>{@link #setRelease(long)} — release fence after write</li>
 *   <li>{@link #getAndAddVolatile(long)} — atomic add with volatile semantics</li>
 * </ul>
 * </p>
 */
public final class Sequence extends RightPaddings {
    private static final VarHandle VALUE_VH = Util.findVarHandlePrivate(Sequence.class, "value", long.class);

    public static final long INITIAL_VALUE = -1L;

    /**
     * Creates a new sequence initialized to the given value.
     *
     * @param value initial sequence value
     */
    public Sequence(long value) {
        super(value);
    }

    /**
     * Returns the current sequence value without memory fences.
     *
     * @return current value
     */
    public long getPlain() {
        return value;
    }

    /**
     * Sets the sequence value without memory fences.
     *
     * @param value new value
     */
    public void setPlain(long value) {
        this.value = value;
    }

    /**
     * Sets the sequence value and ensures a release fence after write.
     * Guarantees that all prior writes are visible before this write to other threads.
     *
     * @param value new value
     */
    public void setRelease(long value) {
        this.value = value;
        VarHandle.releaseFence();
    }

    /**
     * Returns the sequence value with an acquire fence before read.
     * Guarantees that subsequent reads will see this and prior writes.
     *
     * @return current value
     */
    public long getAcquire() {
        VarHandle.acquireFence();
        return value;
    }


    /**
     * Atomically adds the given value to the sequence using volatile semantics.
     *
     * @param value the value to add
     * @return the previous value before addition
     */
    public long getAndAddVolatile(long value) {
        return (long) VALUE_VH.getAndAdd(this, value);
    }

}
