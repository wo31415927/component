package jdk8.stream;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toList;
import static jdk.concurrent.MyExecutorTest.EXECUTOR_SERVICE;

/** @author chenxiang 2018/12/11 */
@Slf4j
public class MyCompletableFutureTest {

  @Test
  public void testFuture() throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture<Double> future = new CompletableFuture();
    new Thread(
        () -> {
          try {
            int a = 1 / 0;
            // 手动设置结果后，futrue.get()才能取到，否则会一直同步等待
            future.complete(3.14);
          } catch (Exception e) {
            // 异常时手动设置异常，futrue.get()才会抛出ExecutionException
            future.completeExceptionally(e);
          }
        });
    future.get(3, TimeUnit.SECONDS);

    // 使用ForkJoinPool类中单例的对象ForkJoinPool.commonPool()来并行
    future.supplyAsync(() -> Math.sqrt(100));
    // 使用自定义的线程池
    future.supplyAsync(() -> Math.sqrt(100), EXECUTOR_SERVICE);
    // 异常时抛出unchecked exception，CompletionException,一般使用此
    future.join();
    // 异常时抛出checked ExecutionException
    //    future.get();
  }

  @Test
  public void testFutureCompose() {
    List<String> shops = Lists.newArrayList("A");
    List<CompletableFuture<String>> futures =
        shops
            .stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(
                            () -> {
                              // 线程t1
                              log.info("supplyAsync");
                              return "Hello" + shop;
                            },
                            EXECUTOR_SERVICE)
                        // 传入T，返回T
                        .thenApply(
                            shop1 -> {
                              // 线程t1
                              log.info("thenApply");
                              return shop1 + ".";
                            })
                        // 在原有线程的基础上异步执行
                        // 传入T，返回T
                        .thenApplyAsync(
                            shop2 -> {
                              log.info("thenApplyAsync");
                              return shop2 + ",";
                            },
                            EXECUTOR_SERVICE)
                        // CompletableFuture实现了CompletionStage接口，所以具备了thenCompose的能力
                        // 传入T，返回CompletableFuture<T>
                        .thenCompose(
                            shop3 ->
                                CompletableFuture.supplyAsync(
                                    () -> {
                                      log.info("thenCompose");
                                      return shop3 + ";";
                                    },
                                    EXECUTOR_SERVICE))
                        // 异步传入T，返回CompletableFuture<T>，相当于异步之后又异步，什么场景下会用到？
                        .thenComposeAsync(
                            shop4 -> {
                              // 使用单独的线程执行CompletableFuture.supplyAsync
                              log.info("thenComposeAsync");
                              return CompletableFuture.supplyAsync(
                                  () -> {
                                    log.info("thenComposeAsyncInternal");
                                    return shop4 + "'";
                                  },
                                  EXECUTOR_SERVICE);
                            },
                            EXECUTOR_SERVICE)
                        // 传入CompletableFuture<T>和BiFunction，按BiFunction处理这2个Future的结果
                        .thenCombine(
                            CompletableFuture.supplyAsync(
                                () -> {
                                  log.info("thenCombine");
                                  return "_";
                                },
                                EXECUTOR_SERVICE),
                            (s1, s2) -> {
                              // 由上一步thenComposeAsync所在的线程执行
                              log.info("combineBiFunc");
                              return s1 + s2;
                            })
                        .thenCombineAsync(
                            CompletableFuture.supplyAsync(
                                () -> {
                                  log.info("thenCombineAsync");
                                  return "=";
                                },
                                EXECUTOR_SERVICE),
                            (s1, s2) -> {
                              // thenCombineAsync使用单独的线程进行合并计算
                              log.info("combineAsyncBiFunc");
                              return s1 + s2;
                            },
                            EXECUTOR_SERVICE))
            .collect(toList());
    // main线程首先收集到future，等待获取所有的结果
    List<String> result = futures.stream().map(CompletableFuture::join).collect(toList());
    log.info(result.toString());
    // 转换成array，注意thenAccept返回的是CompletableFuture<Void>，除了等待无法做别的操作，拿到时已经由thenAccept接受的consumer处理完了
    CompletableFuture[] futures1 =
        futures
            .stream()
            .map(f -> f.thenAccept(System.out::println))
            .toArray(CompletableFuture[]::new);
    // 相比futures.stream().map(CompletableFuture::join).collect(toList());下面的方式有anyOf，更灵活一些
    // 等待所有结束
    CompletableFuture.allOf(futures1).join();
    // 等待任意一个结束
    CompletableFuture.anyOf(futures1).join();
  }

  /** 测试allOf多个future，有一个失败是否会立即返回 */
  @Test(expected = CompletionException.class)
  public void testFutureAllofJoin() {
    CompletableFuture<Void> future1 =
        CompletableFuture.supplyAsync(() -> String.valueOf(1 / 0)).thenAccept(System.out::println);
    CompletableFuture<Void> future2 =
        CompletableFuture.supplyAsync(
                () -> {
                  try {
                    TimeUnit.SECONDS.sleep(3);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  return "wait secs success.";
                })
            .thenAccept(System.out::println);
    // 会等所有的future执行完成后再抛出异常
    CompletableFuture.allOf(future1, future2).join();
  }
}
