package jdk.concurrent;

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zeyu 2018/2/7
 */
public class MyExecutorTest {
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