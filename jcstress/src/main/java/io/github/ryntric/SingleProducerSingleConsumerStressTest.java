package io.github.ryntric;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.Z_Result;

import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

@State
@JCStressTest
@Outcome(id = "true", expect = Expect.ACCEPTABLE)
public class SingleProducerSingleConsumerStressTest {
    private final Channel<Object> channel = Channel.mpsc(8192, ProducerWaitStrategyType.SPINNING, ConsumerWaitStrategyType.SPINNING);
    private static final Object DUMMY = new Object();

    private final LongAdder produced = new LongAdder();
    private final LongAdder consumed = new LongAdder();
    private final Consumer<Object> handler = obj -> {
        Objects.requireNonNull(obj);
        consumed.increment();
    };

    @Actor
    public void producer() {
        channel.push(DUMMY);
        produced.increment();
    }

    @Actor
    public void consumer(Z_Result result) {
        while (consumed.longValue() != produced.longValue()) {
            channel.receive(2048, handler);
        }
        result.r1 = true;
    }

}
