package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

import static io.github.ryntric.WaitPolicy.PARKING;
import static io.github.ryntric.WaitPolicy.SPINNING;

/**
 * author: ryntric
 * date: 8/11/25
 * time: 1:45 PM
 **/

@Fork(1)
@Warmup(iterations = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class OneToOneRingBufferBatchPerfTest {
    private static final EventTranslatorOneArg<Event, Object> TRANSLATOR = Event::setPayload;
    private static final EventHandler<Event> HANDLER = new EventHandler<>() {
        @Override
        public void onEvent(Event event, long sequence) {
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
    private static final Object[] DUMMIES = {
            new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(),
    };

    @State(Scope.Thread)
    public static class OneToOneRingBufferState {
        private final RingBuffer<Event> ringBuffer = new RingBuffer<>(Event::new, SequencerType.SINGLE_PRODUCER, SPINNING, 1 << 12);
        private final EventPoller<Event> eventPoller = new EventPoller<>("worker-test", new ThreadGroup("test"), ringBuffer, PARKING, HANDLER, BatchSizeLimit._1_2);

        @Setup
        public void setup() {
            eventPoller.start();
        }

        @TearDown
        public void teardown() {
            eventPoller.shutdown();
        }
    }

    @Benchmark
    @OperationsPerInvocation(16)
    public void producer(OneToOneRingBufferState state) {
        state.ringBuffer.publishEvents(TRANSLATOR, DUMMIES);
    }

    public static class Event {
        private Object payload;

        public void setPayload(Object payload) {
            this.payload = payload;
        }
    }


}
