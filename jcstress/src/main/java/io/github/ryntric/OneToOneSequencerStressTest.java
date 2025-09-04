package io.github.ryntric;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.Z_Result;

import java.util.concurrent.atomic.LongAdder;

@State
@JCStressTest
@Outcome(id = "true", expect = Expect.ACCEPTABLE)
public class OneToOneSequencerStressTest {
    private static final Object DUMMY = new Object();

    private final LongAdder produced = new LongAdder();
    private final LongAdder consumed = new LongAdder();
    private final EventHandler<Object> handler = new EventHandler<>() {
        @Override
        public void onEvent(String name, Object event, long sequence) {
            consumed.increment();
        }

        @Override
        public void onError(String name, Object event, long sequence, Throwable ex) {

        }

        @Override
        public void onStart(String name) {

        }

        @Override
        public void onShutdown(String name) {

        }
    };

    private final OnHeapRingBuffer<Object> ringBuffer = new OnHeapRingBuffer<>(Object::new, SequencerType.SINGLE_PRODUCER, WaitPolicy.SPINNING, 8192);
    private final EventPoller<Object> eventPoller = new EventPoller<>(ringBuffer, BatchSizeLimit._1_1);

    @Actor
    public void producer() {
        ringBuffer.publishEvent((event, arg) -> {}, DUMMY);
        produced.increment();
    }

    @Actor
    public void consumer(Z_Result result) {
        while (consumed.longValue() != produced.longValue()) {
            eventPoller.poll(null, handler);
        }
        result.r1 = true;
    }

}
