package io.github.ryntric;

import java.util.function.Consumer;

interface Poller<T> {

    PollState poll(Sequencer sequencer, RingBuffer<T> ringBuffer, long batchSize, Consumer<T> consumer);

}
