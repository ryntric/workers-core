package io.github.ryntric;


interface ConsumerWaitStrategy {
    void await();

    void signal();
}
