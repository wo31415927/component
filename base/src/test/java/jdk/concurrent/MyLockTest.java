package jdk.concurrent;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

/** @author chenxiang 2019/6/12/012 */
@Slf4j
public class MyLockTest {
  /** 测试流程： 读1->写1->读2，写1请求时应当等待读1，那么读2请求时是会等待写1还是直接可读？可以直接读 */
  @Test
  public void testReadWriteLock() throws ExecutionException, InterruptedException {
    // https://examples.javacodegeeks.com/core-java/util/concurrent/locks-concurrent/readwritelock/java-readwritelock-example/
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    Lock sLock = readWriteLock.readLock();
    Lock xLock = readWriteLock.writeLock();
    sLock.lock();
    log.info("sLock get 1.");
    Future<?> future =
        CompletableFuture.runAsync(
            () -> {
              log.info("xLock acquire 1.");
              try {
                log.info("xLock get 1,result:{}", xLock.tryLock(3, TimeUnit.SECONDS));
              } catch (InterruptedException e) {
                log.warn(e.toString());
              }
              log.info("xLock end 1.");
            });
    // wait x-lock enqueue
    TimeUnit.SECONDS.sleep(1);
    log.info("sLock acquire 2.");
    sLock.lock();
    log.info("sLock get 2.");
    future.get();
  }
}
