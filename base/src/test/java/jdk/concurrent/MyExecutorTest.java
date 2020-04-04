package jdk.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** @author zeyu 2018/2/7 */
public class MyExecutorTest {
  public static final int THREAD_POOL_MAX = Runtime.getRuntime().availableProcessors() * 2;
  public static final ThreadFactory PARALLEL_THREAD_FACTORY =
      new ThreadFactory() {
        int index = 0;

        @Override
        public Thread newThread(Runnable r) {
          return new Thread(r, "parallel-" + ++index);
        }
      };
  public static final ListeningExecutorService EXECUTOR_SERVICE =
      MoreExecutors.listeningDecorator(
          new ThreadPoolExecutor(
              THREAD_POOL_MAX,
              THREAD_POOL_MAX,
              0L,
              TimeUnit.SECONDS,
              new LinkedTransferQueue<>(),
              PARALLEL_THREAD_FACTORY));
  ScheduledExecutorService singleScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
  ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);

  @Test
  public void testFailedTask() throws Exception {
    AtomicInteger index = new AtomicInteger();
    singleScheduledExecutor.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            if (0 == index.incrementAndGet() % 5) {
              int a = 1 / 0;
            }
            System.out.println(index.get());
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
    singleScheduledExecutor.awaitTermination(60, TimeUnit.DAYS);
  }

    /**
     * 观察execute()执行逻辑
     * 1）未超过coreSize时增加新线程去执行task
     * 2）超过coreSize时直接将task放入queue中
     * 3）queue满时尝试添加新线程(不超过max)来处理新task，非公平，queue中的task反而会最后处理
     * 4）queue满且达到maxSize时执行rejectHandle，默认是抛出异常
     * 5）queue不宜太大，否则不会创建max线程，另外容易内存溢出
     * @throws InterruptedException
     */
  @Test
  public void testCoreSize() throws InterruptedException {
    ExecutorService executorService =
        new ThreadPoolExecutor(
            1, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2), PARALLEL_THREAD_FACTORY);
    int count = 8;
    for (int i = 0; i < count; i++) {
      executorService.execute(
          () -> {
            try {
              Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
    }
    executorService.shutdown();
    executorService.awaitTermination(1800, TimeUnit.SECONDS);
  }
}
