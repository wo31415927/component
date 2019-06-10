package jdk.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zeyu 2018/2/7
 */
public class MyExecutorTest {
    public static final int THREAD_POOL_MAX = Runtime.getRuntime().availableProcessors() * 2;
    public static final ThreadFactory PARALLEL_THREAD_FACTORY = new ThreadFactory() {
        int index = 0;
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"parallel-" + ++index);
        }
    };
    public static final ListeningExecutorService EXECUTOR_SERVICE =
            MoreExecutors.listeningDecorator(new ThreadPoolExecutor(
                    THREAD_POOL_MAX,
                    THREAD_POOL_MAX,
                    0L,
                    TimeUnit.SECONDS,
                    new LinkedTransferQueue<>(), PARALLEL_THREAD_FACTORY));
    ScheduledExecutorService singleScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);

    @Test
    public void testFailedTask() throws Exception {
        AtomicInteger index = new AtomicInteger();
        singleScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(0 == index.incrementAndGet() % 5){
                    int a = 1/0;
                }
                System.out.println(index.get());
            }
        },0,1, TimeUnit.SECONDS);
        singleScheduledExecutor.awaitTermination(60,TimeUnit.DAYS);
    }
}