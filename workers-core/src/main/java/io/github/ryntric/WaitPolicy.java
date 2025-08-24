package io.github.ryntric;

import java.util.concurrent.locks.LockSupport;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 5:49 PM
 **/

public enum WaitPolicy {
    PARKING {
        @Override
        protected void await() {
            LockSupport.parkNanos(1L);
        }
    },
    SPINNING {
        @Override
        protected void await() {
            Thread.onSpinWait();
        }
    },
    YIELDING {
        @Override
        protected void await() {
            Thread.yield();
        }
    };

    protected abstract void await();


}
