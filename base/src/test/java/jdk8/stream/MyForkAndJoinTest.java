package jdk8.stream;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/** @author chenxiang 2018/12/7 */
public class MyForkAndJoinTest {
  /**
   * @param n
   * @return
   */
  public static long testForkAndJoin(long n) {
    long[] numbers = LongStream.rangeClosed(1, n).toArray();
    ForkJoinTask<Long> task = new MyForkAndJoin.ForkJoinSumCalculator(numbers);
    return new ForkJoinPool().invoke(task);
  }
}
