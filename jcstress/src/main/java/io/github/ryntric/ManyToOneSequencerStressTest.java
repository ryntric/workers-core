package io.github.ryntric;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Mode;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.Signal;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.L_Result;
import org.openjdk.jcstress.infra.results.Z_Result;

import java.util.concurrent.atomic.LongAdder;

/**
 * author: vbondarchuk
 * date: 8/25/25
 * time: 1:28 PM
 **/

@State
@JCStressTest
@Outcome(id = "true", expect = Expect.ACCEPTABLE)
public class ManyToOneSequencerStressTest {
    private static final Object DUMMY = new Object();

    private final LongAdder produced = new LongAdder();
    private final LongAdder consumed = new LongAdder();
    private final EventHandler<Object> handler = new EventHandler<>() {
        @Override
        public void onEvent(Object event, long sequence) {
            consumed.increment();
        }

        @Override
        public void onError(Throwable ex) {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onShutdown() {

        }
    };

    private final RingBuffer<Object> ringBuffer = new RingBuffer<>(Object::new, SequencerType.MULTI_PRODUCER, WaitPolicy.SPINNING, 8192);
    private final EventPoller<Object> eventPoller = new EventPoller<>(ringBuffer, BatchSizeLimit._1_1);

    @Actor
    public void producer1() {
        ringBuffer.publishEvent((event, arg) -> {}, DUMMY);
        produced.increment();
    }

    @Actor
    public void producer2() {
        ringBuffer.publishEvent((event, arg) -> {}, DUMMY);
        produced.increment();
    }

    @Actor
    public void producer3() {
        ringBuffer.publishEvent((event, arg) -> {}, DUMMY);
        produced.increment();
    }

    @Actor
    public void producer4() {
        ringBuffer.publishEvent((event, arg) -> {}, DUMMY);
        produced.increment();
    }

    @Actor
    public void consumer(Z_Result result) {
        while (consumed.longValue() != produced.longValue()) {
            eventPoller.poll(handler);
        }
        result.r1 = true;
    }

}
