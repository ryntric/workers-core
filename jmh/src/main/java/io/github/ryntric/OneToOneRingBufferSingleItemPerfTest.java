package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

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
public class OneToOneRingBufferSingleItemPerfTest {
    private static final EventTranslatorOneArg<Event, Object> TRANSLATOR = Event::setPayload;
    private static final Object DUMMY_VALUE = new Object();
    private static final EventHandler<Event> HANDLER = new EventHandler<>() {
        @Override
        public void onEvent(Event event, long sequence) {
        }

        @Override
        public void onError(Event event, long sequence, Throwable ex) {

        }

        @Override
        public void onStart() {
        }

        @Override
        public void onShutdown() {
        }
    };

    @State(Scope.Thread)
    public static class RingBufferState {
        private final AbstractRingBuffer<Event> ringBuffer = new OnHeapRingBuffer<>(Event::new, SequencerType.SINGLE_PRODUCER, SPINNING, 1 << 12);
        private final Worker<Event> worker = new Worker<>("worker-test", new ThreadGroup("test"), SPINNING, HANDLER, BatchSizeLimit._1_2, ringBuffer);

        @Setup
        public void setup() {
            worker.start();
        }

        @TearDown
        public void teardown() {
            worker.shutdown();
        }
    }

    @Benchmark
    public void producer(RingBufferState state) {
        state.worker.publishEvent(TRANSLATOR, DUMMY_VALUE);
    }

    public static class Event {
        private Object payload;

        public void setPayload(Object payload) {
            this.payload = payload;
        }
    }

}
