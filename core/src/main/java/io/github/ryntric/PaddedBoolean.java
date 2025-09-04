package io.github.ryntric;

import io.github.ryntric.util.Util;

import java.lang.invoke.VarHandle;

/**
 * author: ryntric
 * date: 8/25/25
 * time: 6:11 PM
 **/

abstract class LeftBooleanPaddings {
    protected byte
            p10, p11, p12, p13, p14, p15, p16,
            p17, p20, p21, p22, p23, p24, p25,
            p26, p27, p30, p31, p32, p33, p34,
            p35, p36, p37, p40, p41, p42, p43,
            p44, p45, p46, p47, p50, p51, p52,
            p53, p54, p55, p56, p57, p60, p61,
            p62, p63, p64, p65, p66, p67, p70,
            p71, p72, p73, p74, p75, p76, p77,
            p80, p81, p82, p83, p84, p85, p86;
}

abstract class PaddedBooleanValue extends LeftBooleanPaddings {
    protected boolean value;
}

abstract class RightBooleanPaddings extends PaddedBooleanValue {
    protected byte
            p10, p11, p12, p13, p14, p15, p16,
            p17, p20, p21, p22, p23, p24, p25,
            p26, p27, p30, p31, p32, p33, p34,
            p35, p36, p37, p40, p41, p42, p43,
            p44, p45, p46, p47, p50, p51, p52,
            p53, p54, p55, p56, p57, p60, p61,
            p62, p63, p64, p65, p66, p67, p70,
            p71, p72, p73, p74, p75, p76, p77,
            p80, p81, p82, p83, p84, p85, p86;
}

/**
 * A padded boolean value used in concurrent data structures to avoid false sharing.
 * <p>
 * The {@code value} field is padded on both sides using left and right padding
 * classes to ensure it occupies its own cache line. This prevents performance
 * degradation when multiple threads modify adjacent fields.
 * </p>
 *
 * <p>
 * Access methods support different memory semantics:
 * <ul>
 *   <li>{@link #getPlain()} / {@link #setPlain(boolean)} — plain read/write without fences</li>
 *   <li>{@link #getAcquire()} — acquire fence before read</li>
 *   <li>{@link #setRelease(boolean)} — release fence after write</li>
 *   <li>{@link #compareAndSetVolatile(boolean, boolean)} — atomic compare-and-set with volatile semantics</li>
 * </ul>
 * </p>
 */
public final class PaddedBoolean extends RightBooleanPaddings {
    private static final VarHandle VALUE_VH = Util.findVarHandlePrivate(PaddedBoolean.class, "value", boolean.class);

    /**
     * Sets the value without any memory fences.
     *
     * @param value the boolean value to set
     */
    public void setPlain(boolean value) {
        this.value = value;
    }

    /**
     * Returns the value without any memory fences.
     *
     * @return the current value
     */
    public boolean getPlain() {
        return value;
    }

    /**
     * Sets the value with a release fence, ensuring that all previous writes
     * are visible to other threads before this write.
     *
     * @param value the boolean value to set
     */
    public void setRelease(boolean value) {
        this.value = value;
        VarHandle.releaseFence();
    }

    /**
     * Returns the value with an acquire fence, ensuring that subsequent reads
     * see this and previous writes.
     *
     * @return the current value
     */
    public boolean getAcquire() {
        VarHandle.acquireFence();
        return value;
    }

    /**
     * Atomically sets the value to the given updated value if the current value
     * {@code value == expect}. Uses volatile semantics for memory ordering.
     *
     * @param expect the expected current value
     * @param value  the new value to set if the expectation is met
     * @return true if successful, false otherwise
     */
    public boolean compareAndSetVolatile(boolean expect, boolean value) {
        return VALUE_VH.compareAndSet(this, expect, value);
    }

}


