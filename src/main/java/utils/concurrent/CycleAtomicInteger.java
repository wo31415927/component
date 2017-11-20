package utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 循环Atomic
 * @author zeyu
 * 2016/12/20
 */
public class CycleAtomicInteger {
    private final static long PARK_TIME = 100L * 1000;
    private AtomicInteger counter = new AtomicInteger(-1);
    private final int range;

    public CycleAtomicInteger(int range) {
        if (range < 1) {
            throw new IllegalArgumentException("Illegal input range: " + range);
        }
        this.range = range;
    }

    /**
     * 获取下个原子值
     */
    public int next() {
        if(1 == range){
            return 0;
        }
        for (; ; ) {
            int c = counter.get();
            int next = (c + 1) % range;
            if (counter.compareAndSet(c, next)) {
                return next;
            } else {
                LockSupport.parkNanos(PARK_TIME);
            }
        }
    }

    @Override
    public String toString() {
        return "CycleAtomicInteger{" +
                ", range=" + range +
                '}';
    }
}
