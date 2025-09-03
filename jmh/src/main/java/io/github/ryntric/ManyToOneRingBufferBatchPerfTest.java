package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
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
public class ManyToOneRingBufferBatchPerfTest {
    private static final EventTranslatorOneArg<Event, Object> TRANSLATOR = Event::setPayload;
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
    private static final Object[] DUMMIES = {
            new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(), new Object(),
    };

    @State(Scope.Group)
    public static class OneToOneRingBufferState {
        private final AbstractRingBuffer<Event> ringBuffer = new OnHeapRingBuffer<>(Event::new, SequencerType.MULTI_PRODUCER, SPINNING, 1 << 12);
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
    @Group("manyToOne")
    @OperationsPerInvocation(16)
    public void producer1(OneToOneRingBufferState state) {
        state.worker.publishEvents(TRANSLATOR, DUMMIES);
    }

    @Benchmark
    @Group("manyToOne")
    @OperationsPerInvocation(16)
    public void producer2(OneToOneRingBufferState state) {
        state.worker.publishEvents(TRANSLATOR, DUMMIES);
    }

    @Benchmark
    @Group("manyToOne")
    @OperationsPerInvocation(16)
    public void producer3(OneToOneRingBufferState state) {
        state.worker.publishEvents(TRANSLATOR, DUMMIES);
    }

    @Benchmark
    @Group("manyToOne")
    @OperationsPerInvocation(16)
    public void producer4(OneToOneRingBufferState state) {
        state.worker.publishEvents(TRANSLATOR, DUMMIES);
    }

    public static class Event {
        private Object payload;

        public void setPayload(Object payload) {
            this.payload = payload;
        }
    }


}
