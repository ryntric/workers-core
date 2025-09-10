package io.github.ryntric;

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

/**
 * A sequencer for many-to-one (multi-producer, single-consumer) scenarios.
 * <p>
 * This sequencer supports multiple producers claiming sequences concurrently,
 * while a single consumer reads them. It uses an {@link AvailabilityBuffer} to
 * track published sequences and padding to avoid false sharing.
 */
public final class ManyToOneSequencer extends ManyToOneSequencerRightPaddings {
    private final AvailabilityBuffer availabilityBuffer;

    /**
     * Creates a new ManyToOneSequencer.
     *
     * @param waitPolicy the waiting strategy used by producers
     * @param bufferSize the size of the ring buffer
     */
    public ManyToOneSequencer(WaitPolicy waitPolicy, int bufferSize) {
        super(waitPolicy, bufferSize);
        this.availabilityBuffer = new AvailabilityBuffer(bufferSize);
    }

    @Override
    public long next(int n) {
        int bufferSize = this.bufferSize;
        checkConstraintOfClaimedValue(n, bufferSize);

        long cached = this.cached;
        long next = cursorSequence.getAndAddVolatile(n) + n;
        long wrapPoint = next - bufferSize;

        if (wrapPoint > cached) {
            this.cached = await(gatingSequence, wrapPoint);
        }

        return next;
    }

    @Override
    public void publish(long sequence) {
        availabilityBuffer.set(sequence);
    }

    @Override
    public void publish(long low, long high) {
        availabilityBuffer.setRange(low, high);
    }

    @Override
    public long getHighestPublishedSequence(long next, long available) {
        return availabilityBuffer.getAvailable(next, available);
    }
}
