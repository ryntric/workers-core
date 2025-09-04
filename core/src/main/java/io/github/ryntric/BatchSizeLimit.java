package io.github.ryntric;

/**
 * author: ryntric
 * date: 8/13/25
 * time: 11:40 AM
 * Defines batch size limits as integer reductions of a given size.
 * <p>
 * Each constant represents a divisor of the {@link  AbstractRingBuffer#size()}.
 * For example, {@link #_1_2} returns half of the given size,
 * {@link #_1_4} returns a quarter, and so on.
 * </p>
 **/

public enum BatchSizeLimit {
    _1_1 {
        @Override
        public int get(int size) {
            return size;
        }
    },
    _1_2 {
        @Override
        public int get(int size) {
            return size >> 1;
        }
    },
    _1_4 {
        @Override
        public int get(int size) {
            return size >> 2;
        }
    },
    _1_8 {
        @Override
        public int get(int size) {
            return size >> 3;
        }
    },
    _1_16 {
        @Override
        public int get(int size) {
            return size >> 4;
        }
    };

    public abstract int get(int size);
}
