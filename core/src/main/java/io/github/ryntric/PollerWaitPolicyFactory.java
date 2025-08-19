package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/17/25
 * time: 5:49 PM
 **/

public final class PollerWaitPolicyFactory {

    public static PollerWaitPolicy create(WaitingPolicyType type) {
        PollerWaitPolicy policy = null;
        switch (type) {
            case BLOCKING:
                policy = new PollerBlockingPolicy();
            case PARKING:
                policy = new PollerParkingPolicy();
            case SPINNING:
                policy = new PollerSpinningPolicy();
            case YIELDING:
                policy = new PollerYieldingPolicy();
            case LOOPING:
                policy = new PollerLoopingPolicy();
        }
        return policy;
    }
}
