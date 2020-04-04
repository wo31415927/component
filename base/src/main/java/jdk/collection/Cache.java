package jdk.collection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

/** @author cctv 2018/1/15 */
@Slf4j
public class Cache<K, V> {

  private ConcurrentMap<K, V> cacheObjMap = new ConcurrentHashMap<K, V>();

  private DelayQueue<DelayItem<Pair<K, V>>> q = new DelayQueue<DelayItem<Pair<K, V>>>();

  private Thread daemonThread;

  public Cache() {

    Runnable daemonTask =
        new Runnable() {
          @Override
          public void run() {
            daemonCheck();
          }
        };

    daemonThread = new Thread(daemonTask);
    daemonThread.setDaemon(true);
    daemonThread.setName("Cache Daemon");
    daemonThread.start();
  }

  private void daemonCheck() {
    for (; ; ) {
      try {
        DelayItem<Pair<K, V>> delayItem = q.take();
        if (delayItem != null) {
          // 超时对象处理
          Pair<K, V> pair = delayItem.getItem();
          cacheObjMap.remove(pair.first, pair.second); // compare and remove
        }
      } catch (InterruptedException e) {
        log.error("", e);
        break;
      }
    }
  }

  public void put(K key, V value, long time, TimeUnit unit) {
    V oldValue = cacheObjMap.put(key, value);
    if (oldValue != null) {
      q.remove(key);
    }

    long nanoTime = TimeUnit.NANOSECONDS.convert(time, unit);
    q.put(new DelayItem<Pair<K, V>>(new Pair<K, V>(key, value), nanoTime));
  }

  public V get(K key) {
    return cacheObjMap.get(key);
  }

  public class Pair<K, V> {
    public K first;

    public V second;

    public Pair() {}

    public Pair(K first, V second) {
      this.first = first;
      this.second = second;
    }
  }

  public static class DelayItem<T> implements Delayed {
    /** Base of nanosecond timings, to avoid wrapping */
    private static final long NANO_ORIGIN = System.nanoTime();

    /** Returns nanosecond time offset by origin */
    static final long now() {
      return System.nanoTime() - NANO_ORIGIN;
    }

    /**
     * Sequence number to break scheduling ties, and in turn to guarantee FIFO order among tied
     * entries.
     */
    private static final AtomicLong sequencer = new AtomicLong(0);

    /** Sequence number to break ties FIFO */
    private final long sequenceNumber;

    /** The time the task is enabled to execute in nanoTime units */
    private final long time;

    private final T item;

    public DelayItem(T submit, long timeout) {
      this.time = now() + timeout;
      this.item = submit;
      this.sequenceNumber = sequencer.getAndIncrement();
    }

    public T getItem() {
      return this.item;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      long d = unit.convert(time - now(), TimeUnit.NANOSECONDS);
      return d;
    }

    @Override
    public int compareTo(Delayed other) {
      if (other == this) {
        return 0;
      }
      if (other instanceof DelayItem) {
        DelayItem x = (DelayItem) other;
        long diff = time - x.time;
        if (diff < 0) {
          return -1;
        } else if (diff > 0) {
          return 1;
        } else if (sequenceNumber < x.sequenceNumber) {
          return -1;
        } else {
          return 1;
        }
      }
      long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
      return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }
  }
}
